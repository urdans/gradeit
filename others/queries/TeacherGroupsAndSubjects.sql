# returns the list of all the groups a teacher belongs to and the subject he is assigned to in that group
SELECT
	groupid, groupname, subjectid, subject.name as subjectname
FROM
	(SELECT
		#* 
		group_id as groupid, `group`.name as groupname, subject_id as subjectid
	FROM
		(SELECT
			grouping.group_id, grouping.subject_id
		FROM
			grouping
		INNER JOIN
			teacher ON teacher.id = grouping.teacher_id
		WHERE
			teacher.id = 1
		) AS teachergroups
	INNER JOIN
		`group` ON `group`.id = teachergroups.group_id) AS teachergroupsandsubjects
INNER JOIN
	`subject` ON `subject`.id = teachergroupsandsubjects.subjectid
