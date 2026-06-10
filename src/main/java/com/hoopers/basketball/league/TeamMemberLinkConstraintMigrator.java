package com.hoopers.basketball.league;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class TeamMemberLinkConstraintMigrator {

	@Bean
	ApplicationRunner migrateTeamMemberLinkConstraint(JdbcTemplate jdbcTemplate) {
		return args -> {
			if (!isPostgres(jdbcTemplate) || !tableExists(jdbcTemplate, "team_roster_members")) {
				return;
			}

			jdbcTemplate.execute("""
					update team_roster_members member
					set user_id = null
					where user_id is not null
					  and id <> (
					    select min(first_member.id)
					    from team_roster_members first_member
					    where first_member.user_id = member.user_id
					  )
					""");
			jdbcTemplate.execute("""
					create unique index if not exists team_roster_members_user_id_unique
					on team_roster_members (user_id)
					where user_id is not null
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
}
