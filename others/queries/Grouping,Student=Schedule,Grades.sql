SELECT
	Date(date) as date, description, percentage, grade.`value` AS gradeValue, grade.id AS gradeId
FROM
	(SELECT
		*
	FROM
		grade
	where
		student_id = 2)
AS
	grade
RIGHT JOIN
	`schedule`
ON
	grade.schedule_id = `schedule`.id
WHERE
	grouping_id = 19
ORDER BY
	date
ASC

    

