# given a teacher Id and the groping he is part of, returns the schedule for that grouping (subject, teacher, group)
SELECT
	id, date, description, percentage
FROM
	(SELECT
		groupingid, groupid, groupname, subjectid, subject.name as subjectname
	FROM
		(SELECT
			#* 
			groupingid, group_id as groupid, `group`.name as groupname, subject_id as subjectid
		FROM
			(SELECT
				grouping.id as groupingid, grouping.group_id, grouping.subject_id
			FROM
				grouping
			INNER JOIN
				teacher ON teacher.id = grouping.teacher_id
			WHERE
				teacher.id = 2 #JOSE
			) AS teachergroups
		INNER JOIN
			`group` ON `group`.id = teachergroups.group_id) AS teachergroupsandsubjects
	INNER JOIN
		`subject` ON `subject`.id = teachergroupsandsubjects.subjectid)
AS
	pairs
INNER JOIN
	`schedule`
ON
	pairs.groupingid = `schedule`.grouping_id
WHERE
	pairs.groupingid = 1 #BIOLOGY