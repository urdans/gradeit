#given the grouping id, returns all the active students that are part of the group that belongs to the giving grouping id
SELECT
	id, CONCAT(last_name, ', ', first_name) as name
FROM
	student
WHERE
	student.group_id = (
	SELECT
		groupid
	FROM
		(SELECT
			grouping.id, `group`.id as groupid, `group`.`name`
		FROM
			grouping
		INNER JOIN
			`group`
		ON
			grouping.group_id = `group`.id
		WHERE
			grouping.id = 1)
	AS groupinggroups
    )
    AND
    student.active = 1