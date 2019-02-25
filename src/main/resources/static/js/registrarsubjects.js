document.onload = setup();

var Mode = { EDIT : 0, ADD : 1 }

var EditMode = Mode.ADD;
var EditSubjectId = 0;

function setup(){
    forEachEdit(function(element){ element.onclick = editSubject; })
    forEachDelete(function(element){ element.onclick = deleteSubject; })
    document.getElementById('a-link-add').onclick = addSubject;
    document.getElementById('add-edit-button').onclick = addEditSubjectDo;
    document.getElementById('cancel-button').onclick = cancelAddEdit;
}

function deleteSubject(e) {
    // document.getElementById('cell-form').submit();
    subjectToDeleteId = parseInt(String(e.target.id).substr(9));
    deleteExistingSubject({"id" : subjectToDeleteId, "name": ""});
}

function deleteSubjectAfterPost(response, subject) {
    if(response) {
        document.getElementById("rowid-" + subject.id).outerHTML = "";
        document.getElementById('errormsg').innerText = "Subject properly deleted";
    }
    else document.getElementById('errormsg').innerText = "Unable to delete this subject";

    document.getElementById('errormsg').hidden = false;
    setTimeout(()=>{document.getElementById('errormsg'). hidden = true;}, 1500);
}

function editSubject(e) {
    document.getElementById('subject-form').innerText = "Editing an existing subject";
    document.getElementById('add-edit-button').innerText = "Save";
    EditSubjectId = parseInt(String(e.target.id).substr(7));
    getSubjectName(EditSubjectId); //calling the api; Once the api returns, the editSubjectContinue function will be called
}

function editSubjectContinue(subjectObject){
    EditMode = Mode.EDIT;
    editFormShow(true);
    document.getElementById('subject-name-box').value = subjectObject.name;
}

function addSubject () {
    document.getElementById('subject-form').innerText = "Adding an new subject";
    document.getElementById('add-edit-button').innerText = "Add";   
    document.getElementById('subject-name-box').value = "";
    editFormShow(true);
    EditMode = Mode.ADD;
}

function addEditSubjectDo(e) {
    if(EditMode == Mode.EDIT) {
        //use ajax to call a rest api to update this field id with the edited subject name
        //console.log("Editing subject id = " + EditSubjectId + " ...");
        updateSubject({"id" : EditSubjectId, "name": document.getElementById('subject-name-box').value});
    } else if(EditMode == Mode.ADD) {
        //this is a way of submitting data as a form (not json data)
        /* var form = document.createElement("form");
        form.setAttribute("method", "POST");
        form.setAttribute("action", "/registrar/subjects/add");
        var hiddenField = document.createElement("input");
        hiddenField.setAttribute("type", "hidden");
        hiddenField.setAttribute("name", "name");
        hiddenField.setAttribute("value", document.getElementById('subject-name-box').value);
        form.appendChild(hiddenField);
        document.body.appendChild(form);
        form.submit(); */
        //use ajax to call a rest api to add this the new subject name
        addNewSubject({"id" : -1, "name": document.getElementById('subject-name-box').value});
    }
    editFormShow(false);
}

function editSubjectEditAfterPost(response, subject) {
    if(response) {
        document.getElementById(subject.id).innerText = subject.name;
        document.getElementById('errormsg').innerText = "Subject properly updated";
    }
    else document.getElementById('errormsg').innerText = "Unable to updated this subject";

    document.getElementById('errormsg').hidden = false;
    setTimeout(()=>{document.getElementById('errormsg'). hidden = true;}, 1500);
}

function editSubjectAddAfterPost(response, subject) {
    if(response > 0) {
        var table = document.getElementById("subject-table");
        var row = table.insertRow(table.rows.length - 1);
        row.setAttribute("id", "rowid-" + subject.id);
        var subjectName = row.insertCell(0);
        subjectName.setAttribute("id", subject.id);
        subjectName.innerHTML = subject.name;
        row.insertCell(1).innerHTML = '<a id="editId-' + subject.id + '" href="#">Edit</a>';
/*         row.insertCell(2).innerHTML = '<form id="cell-form" method="post">\n' +
                                      '<input type="hidden" value="' + subject.id + '">\n' +
                                      '<a id="deleteId-' + subject.id + '" href="#">Delete</a>\n' +
                                      '</form>'; */
        row.insertCell(2).innerHTML = '<a id="deleteId-' + subject.id + '" href="#">Delete</a>'; 
        document.getElementById("editId-" + subject.id).onclick = editSubject;
        document.getElementById("deleteId-" + subject.id).onclick = deleteSubject;

        document.getElementById('errormsg').innerText = "Subject properly added";
    }
    else document.getElementById('errormsg').innerText = "Unable to add this subject";

    document.getElementById('errormsg').hidden = false;
    setTimeout(()=>{document.getElementById('errormsg'). hidden = true;}, 1500);
}

function cancelAddEdit() {
    editFormShow(false);
}

function editFormShow(state) {
    var addLink = document.getElementById('a-link-add');
    var editForm = document.getElementById('edit-form');
    editForm.hidden = !state;
    addLink.hidden = !editForm.hidden;
    
    if (state) {
        forEachEdit(function(element){ element.removeAttribute("href"); })
        forEachDelete(function(element){ element.removeAttribute("href"); })
    }else{
        forEachEdit(function(element){ element.setAttribute("href","#"); })
        forEachDelete(function(element){ element.setAttribute("href","#"); })
    }
}

function forEachEdit(doForEach){
    var tbodyE = document.getElementById("subject-list");
    var aList = tbodyE.getElementsByTagName("A");
    var L = aList.length;
    for(i = 0; i < L; i++){
        if(aList[i].id.startsWith("editId-")) {
            doForEach(aList[i]);
        }
    }
}

function forEachDelete(doForEach){
    var tbodyE = document.getElementById("subject-list");
    var aList = tbodyE.getElementsByTagName("A");
    var L = aList.length;
    for(i = 0; i < L; i++){
        if(aList[i].id.startsWith("deleteId-")) {
            doForEach(aList[i]);
        }
    }
}

function getSubjectName(id) {
    callApi('GET', 'http://localhost:8080/api/getsubject?id=' + id, function (dataReturned){
        if(dataReturned != null) {
            editSubjectContinue(dataReturned);
        }
    });
}

function updateSubject(subjectObject) {
    callApi('PUT', 'http://localhost:8080/api/updatesubject', function (dataReturned){
        if(dataReturned != null) {
            editSubjectEditAfterPost(dataReturned, subjectObject);
        }
    }, subjectObject);
}

function addNewSubject(subjectObject) {
    callApi('POST', 'http://localhost:8080/api/addsubject', function (dataReturned){
        if(dataReturned != null) {
            subjectObject.id = dataReturned;
            editSubjectAddAfterPost(dataReturned, subjectObject);
        }
    }, subjectObject);
}

function deleteExistingSubject(subjectObject) {
    callApi('DELETE', 'http://localhost:8080/api/deletesubject', function (dataReturned){
        if(dataReturned != null) {
            deleteSubjectAfterPost(dataReturned, subjectObject);
        }
    }, subjectObject);
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