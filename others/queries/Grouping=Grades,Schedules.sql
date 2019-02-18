#given the grouping id, shows all the grades for all students related to that
SELECT
	`schedule`.id as scheduleid, date(`schedule`.date)  as scheduledate, `schedule`.description as scheduledescription,
    `schedule`.percentage as schedulepercentage, grade.value as gradevalue, grade.id as gradeid, student_id
FROM
	grade
INNER JOIN
	`schedule`
ON
	grade.schedule_id = `schedule`.id
WHERE
	grouping_id = 1
#AND
#	student_id = 1