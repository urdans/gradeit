document.onload = setup();


function setup(){
/*     forEachRemove(function(element){ element.onclick = removeGrouping; })
    document.getElementById('add-group').onclick = addGroup;
    document.getElementById('edit-group').onclick = editGroup;
    document.getElementById('delete-group').onclick = deleteGroup;
    document.getElementById('a-link-add').onclick = addGrouping;
    document.getElementById('group-cancel-button').onclick = groupCancel;
    document.getElementById('cancelgrouping-button').onclick = groupingCancel; */
    document.getElementById('selectedPair').onchange = updatePage;
    setUpTitle();
}

function updatePage(e) {
    document.getElementById("groupSubjectPairForm").submit();
}

function setUpTitle() {
    var s = document.getElementById("selectedPair").selectedOptions[0].innerText;
    var s1 = String(s).split(" - ");
    document.getElementById("roster-title").innerText = s1[0] + " roster for " + s1[1];
}

/* 
function addGroup(e) {
    document.getElementById("group-form-title").innerText = "Adding a new group name";
    document.getElementById("group-name-box").value = "";
    document.getElementById("action-button").innerText = "Add";
    setUpGroupForm(true);
    document.getElementById("action-button").onclick = function() {
        var newGroupName = document.getElementById("group-name-box").value;
        if(newGroupName.length < 1) {
            showMessage("group-msg", "Group name can not be empty!");
            return;
        }
        callApi('POST', 'http://localhost:8080/api/addgroup', function (dataReturned) {
            if(dataReturned) {
                showMessage("group-msg", 'Group name "' + newGroupName + '" added!');
                var groupSelector = document.getElementById("groupId");
                groupSelector.options[groupSelector.options.length] = new Option(newGroupName, dataReturned);
            }
            else showMessage("group-msg", "Unknown error. Group name not added!");
            document.getElementById("group-name-box").value = "";
            document.getElementById("group-name-box").focus();
        }, {"id" : 0, "name": newGroupName});
    }
}

function editGroup(e) {
    document.getElementById("group-form-title").innerText = "Editing an existing group name";
    document.getElementById("group-name-box").value = document.getElementById("groupId").selectedOptions[0].innerText;
    document.getElementById("action-button").innerText = "Save";
    setUpGroupForm(true);
    document.getElementById("action-button").onclick = function() {
        var groupName = document.getElementById("group-name-box").value;
        var groupId = document.getElementById("groupId").value;
        if(groupName.length < 1) {
            showMessage("group-msg", "Group name can not be empty!");
            return;
        }
        callApi('PUT', 'http://localhost:8080/api/updategroup', function (dataReturned) {
            if(dataReturned) {
                showMessage("delete-group-msg", "Group name saved!");
                var selectedGroup = document.getElementById("groupId").selectedOptions[0];
                selectedGroup.innerText = groupName;
            }
            else showMessage("delete-group-msg", "Unknown error. Group name not saved!");
            setUpGroupForm(false);
         }, {"id" : groupId, "name": groupName});
    }
}

function deleteGroup(e) {
    var selectedGroup = document.getElementById("groupId").selectedOptions[0];
    groupName = selectedGroup.innerText;
    groupId = selectedGroup.value;
    callApi('DELETE', 'http://localhost:8080/api/deletegroup', function (dataReturned) {
        showMessage("delete-group-msg", dataReturned.message);
        if(!dataReturned.isError) {
            var groupSelector = document.getElementById("groupId");
            groupSelector.remove(groupSelector.selectedIndex);
            updatePage(null);
        }
    }, {"id" : groupId, "name": groupName});
}

function addGrouping(e) {
    document.getElementById("subjects").selectedIndex = 0;
    document.getElementById("teachers").selectedIndex = 0;
    setUpGroupingForm(true);
    document.getElementById("addgrouping-button").onclick = function() {
        var groupId = document.getElementById("groupId").selectedOptions[0].value;
        var subjectId = document.getElementById("subjects").selectedOptions[0].value;
        var teacherId = document.getElementById("teachers").selectedOptions[0].value;
        var subject = document.getElementById("subjects").selectedOptions[0].innerText;
        var teacher = document.getElementById("teachers").selectedOptions[0].innerText;
        if(subjectId == 0 || teacherId == 0) {
            showMessage("grouping-msg", "Please select one subject and one teacher!");
            return;
        }
        console.log("group id: " + groupId + "    subjectId: " + subjectId + "    teacherId: " + teacherId);
        console.log("Subject: " + subject + "      Teacher: " + teacher);
        callApi('POST', 'http://localhost:8080/api/addgrouping', function (dataReturned) {
            showMessage("grouping-msg", dataReturned.message);
            if(!dataReturned.isError) {
                //add the pair to the table
                var table = document.getElementById("grouping-table");
                var row = table.insertRow(table.rows.length - 1);
                row.insertCell(0).innerText = subject;
                row.insertCell(1).innerText = teacher;
                row.insertCell(2).innerHTML = '<a id="removeId-' + dataReturned.id + '" href="#" hidden>Remove</a>';
                document.getElementById("removeId-" + dataReturned.id).onclick = removeGrouping;
            }
        }, {"group_id" : groupId, "subject_id": subjectId, "teacher_id": teacherId});
        document.getElementById("subjects").selectedIndex = 0;
        document.getElementById("teachers").selectedIndex = 0;
    } 
 }

function removeGrouping(e) {
    groupingIdToDeleteId = parseInt(String(e.target.id).substr(9));
    console.log("groupingIdToDeleteId: " + groupingIdToDeleteId);
    callApi('DELETE', 'http://localhost:8080/api/deletegrouping', function (dataReturned) {
        showMessage("outer-grouping-msg", dataReturned.message);
        if(!dataReturned.isError) {
            //remove the pair to the table
            document.getElementById("rowid-" + groupingIdToDeleteId).outerHTML = "";
        }
    }, {"group_id" : groupingIdToDeleteId, "subject_id": "", "teacher_id": ""});
}

function groupCancel() {
    setUpGroupForm(false);
}

function groupingCancel() {
    setUpGroupingForm(false);
}

function setUpGroupForm(active) {
    document.getElementById("group-form").hidden = !active;
    document.getElementById("group-option-menu").hidden = active;
    grayer("groupForm", active);
    forEachRemove(function(element){ element.hidden = active; })  
    document.getElementById("a-link-add").hidden = active;  
}

function setUpGroupingForm(active) {
    document.getElementById("add-form").hidden = !active;
    document.getElementById("group-option-menu").hidden = active;
    grayer("groupForm", active);
    forEachRemove(function(element){ element.hidden = active; })  
    document.getElementById("a-link-add").hidden = active; 
}

function showMessage(elementId, message) {
    document.getElementById(elementId).innerText = message;
    document.getElementById(elementId).hidden = false;
    setTimeout(()=>{document.getElementById(elementId). hidden = true;}, 3500);    
}

function grayer(formId, yesNo) {
    var f = document.getElementById(formId), s, opacity;
    s = f.style;
    opacity = yesNo? '40' : '100';
    s.opacity = s.MozOpacity = s.KhtmlOpacity = opacity/100;
    s.filter = 'alpha(opacity='+opacity+')';
    for(var i=0; i<f.length; i++) f[i].disabled = yesNo;
}

function forEachRemove(doForEach){
    var tbodyR = document.getElementById("grouping-list");
    var aList = tbodyR.getElementsByTagName("A");
    var L = aList.length;
    for(i = 0; i < L; i++){
        if(aList[i].id.startsWith("removeId-")) {
            doForEach(aList[i]);
        }
    }
}

function callApi(method, endpoint, callback, data = null){
    var request = new XMLHttpRequest();
    request.open(method, endpoint, true);
    request.onload = function () {
        var data = null;
        if (request.status >= 200 && request.status < 400)  data =  JSON.parse(this.response);
        callback(data);
    }
    if(data != null) {
        request.setRequestHeader("Content-type", "application/json");
        jsonData = JSON.stringify(data);
        request.send(jsonData);
    }
    else request.send();
}
 */

