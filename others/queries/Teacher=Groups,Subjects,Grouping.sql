# given the teacher id, returns all the grouping he is part of, showing the pair group name - subject name.
# the groupingid field is unique
SELECT
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
			teacher.id = 2
		) AS teachergroups
	INNER JOIN
		`group` ON `group`.id = teachergroups.group_id) AS teachergroupsandsubjects
INNER JOIN
	`subject` ON `subject`.id = teachergroupsandsubjects.subjectid
ORDER BY groupname, subjectname