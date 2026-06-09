package com.hoopers.basketball.auth;

import java.util.List;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class RoleConstraintMigrator {

	@Bean
	ApplicationRunner migrateRoleConstraint(JdbcTemplate jdbcTemplate) {
		return args -> {
			if (!isPostgres(jdbcTemplate) || !tableExists(jdbcTemplate, "app_users")) {
				return;
			}

			List<String> constraints = jdbcTemplate.queryForList("""
					select c.conname
					from pg_constraint c
					join pg_class t on t.oid = c.conrelid
					join pg_namespace n on n.oid = t.relnamespace
					where n.nspname = current_schema()
					  and t.relname = 'app_users'
					  and c.contype = 'c'
					  and pg_get_constraintdef(c.oid) like '%role%'
			""", String.class);
			for (String constraint : constraints) {
				jdbcTemplate.execute("alter table app_users drop constraint if exists " + quoteIdentifier(constraint));
			}
			jdbcTemplate.execute("""
					alter table app_users
					add constraint app_users_role_check
					check (role in ('ADMIN', 'COACH', 'MEMBER'))
					""");
			if (tableExists(jdbcTemplate, "user_roles")) {
				jdbcTemplate.execute("""
						insert into user_roles (user_id, role, created_at)
						select id, 'MEMBER', now()
						from app_users
						on conflict (user_id, role) do nothing
						""");
				jdbcTemplate.execute("""
						insert into user_roles (user_id, role, created_at)
						select id, role, now()
						from app_users
						where role in ('ADMIN', 'COACH')
						on conflict (user_id, role) do nothing
						""");
			}
		};
	}

	private boolean isPostgres(JdbcTemplate jdbcTemplate) {
		return jdbcTemplate.queryForObject("select version()", String.class).toLowerCase().contains("postgresql");
	}

	private boolean tableExists(JdbcTemplate jdbcTemplate, String tableName) {
		Boolean exists = jdbcTemplate.queryForObject("""
				select exists (
					select 1
					from information_schema.tables
					where table_schema = current_schema()
					  and table_name = ?
				)
				""", Boolean.class, tableName);
		return Boolean.TRUE.equals(exists);
	}

	private String quoteIdentifier(String identifier) {
		return "\"" + identifier.replace("\"", "\"\"") + "\"";
	}
}
