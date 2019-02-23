SELECT
	Date(date) as date, description, percentage, grade.`value` AS gradeValue, grade.id AS gradeId, `schedule`.id AS scheduleId
FROM
	(SELECT
		*
	FROM
		grade
	where
		student_id = 11)
AS
	grade
RIGHT JOIN
	`schedule`
ON
	grade.schedule_id = `schedule`.id
WHERE
	grouping_id = 19
ORDER BY date ASC

    

