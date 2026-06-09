package com.hoopers.basketball.course;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class CourseSchemaMigrator {

	@Bean
	ApplicationRunner migrateCourseSchema(JdbcTemplate jdbcTemplate) {
		return args -> {
			if (!isPostgres(jdbcTemplate)) {
				return;
			}
			if (tableExists(jdbcTemplate, "courses")) {
				jdbcTemplate.execute("alter table courses add column if not exists price integer");
				jdbcTemplate.execute("update courses set price = 0 where price is null");
				jdbcTemplate.execute("alter table courses alter column price set default 0");
				jdbcTemplate.execute("alter table courses alter column price set not null");

				jdbcTemplate.execute("alter table courses add column if not exists status varchar(20)");
				jdbcTemplate.execute("update courses set status = 'OPEN' where status is null or status = ''");
				jdbcTemplate.execute("alter table courses alter column status set default 'OPEN'");
				jdbcTemplate.execute("alter table courses alter column status set not null");

				jdbcTemplate.execute("alter table courses add column if not exists location varchar(120)");
				jdbcTemplate.execute("update courses set location = '' where location is null");
				jdbcTemplate.execute("alter table courses alter column location set default ''");
				jdbcTemplate.execute("alter table courses alter column location set not null");
			}
			if (tableExists(jdbcTemplate, "course_registrations")) {
				jdbcTemplate.execute("alter table course_registrations add column if not exists status varchar(20)");
				jdbcTemplate.execute("update course_registrations set status = 'REGISTERED' where status is null or status = ''");
				jdbcTemplate.execute("alter table course_registrations alter column status set default 'REGISTERED'");
				jdbcTemplate.execute("alter table course_registrations alter column status set not null");

				jdbcTemplate.execute("alter table course_registrations add column if not exists payment_status varchar(20)");
				jdbcTemplate.execute("update course_registrations set payment_status = 'UNPAID' where payment_status is null or payment_status = ''");
				jdbcTemplate.execute("alter table course_registrations alter column payment_status set default 'UNPAID'");
				jdbcTemplate.execute("alter table course_registrations alter column payment_status set not null");

				jdbcTemplate.execute("alter table course_registrations add column if not exists checked_in boolean");
				jdbcTemplate.execute("update course_registrations set checked_in = false where checked_in is null");
				jdbcTemplate.execute("alter table course_registrations alter column checked_in set default false");
				jdbcTemplate.execute("alter table course_registrations alter column checked_in set not null");
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
}
