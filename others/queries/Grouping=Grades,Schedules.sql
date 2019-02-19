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
	student_id = 4 # 12 #2
    AND grouping_id = 1 #19
#AND
#	`schedule`.id = 13