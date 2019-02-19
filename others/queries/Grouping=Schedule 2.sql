# given the groping id, returns the schedule for that grouping (subject, teacher, group)
SELECT
	* #id, date(`date`), description, percentage
FROM
	`schedule`
WHERE grouping_id = 1