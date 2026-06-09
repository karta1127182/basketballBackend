package com.hoopers.basketball.league;

import java.util.List;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class ScheduleConstraintMigrator {

	@Bean
	ApplicationRunner migrateScheduleStatusConstraint(JdbcTemplate jdbcTemplate) {
		return args -> {
			if (!isPostgres(jdbcTemplate) || !tableExists(jdbcTemplate, "league_schedules")) {
				return;
			}

			List<String> constraints = jdbcTemplate.queryForList("""
					select c.conname
					from pg_constraint c
					join pg_class t on t.oid = c.conrelid
					join pg_namespace n on n.oid = t.relnamespace
					where n.nspname = current_schema()
					  and t.relname = 'league_schedules'
					  and c.contype = 'c'
					  and pg_get_constraintdef(c.oid) like '%status%'
					""", String.class);
			for (String constraint : constraints) {
				jdbcTemplate.execute("alter table league_schedules drop constraint if exists " + quoteIdentifier(constraint));
			}
			jdbcTemplate.execute("""
					alter table league_schedules
					add constraint league_schedules_status_check
					check (status in ('UPCOMING', 'LIVE', 'FINAL'))
					""");
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
