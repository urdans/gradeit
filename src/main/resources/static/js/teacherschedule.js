document.onload = setup();

function setup(){
    forEachIdTag("editId-", function(element){ element.onclick = editSchedule; })
    forEachIdTag("deleteId-", function(element){ element.onclick = deleteSchedule; })
    document.getElementById('a-link-add').onclick = addEvaluation;
    document.getElementById('selectedPair').onchange = updatePage;
    document.getElementById('close-button').onclick = editFormClose;
    setUpTitle();
    updatePercentage();
}

function updatePage(e) {
    document.getElementById("groupSubjectPairForm").submit();
}

function setUpTitle() {
    var s = document.getElementById("selectedPair").selectedOptions[0].innerText;
    var s1 = String(s).split(" - ");
    document.getElementById("roster-title").innerText = s1[1] + " Evaluation Plan for " + s1[0];
}

function addEvaluation(e) {
    function clearBoxes(){
        document.getElementById("description-box").value = "";
        document.getElementById("date-box").value = "";
        document.getElementById("percentage-box").value = "";
    }
    document.getElementById("edit-form-title").innerText = "Adding a new evaluation";
    clearBoxes();
    document.getElementById("action-button").innerText = "Add";
    setUpEditForm(true);
    document.getElementById("action-button").onclick = function() {
        var description = document.getElementById("description-box").value;
        var date = document.getElementById("date-box").value;
        var percentage = document.getElementById("percentage-box").value;
        var groupingId = document.getElementById("selectedPair").selectedOptions[0].value;
        if(description.length <= 2) {
            showMessage("edit-msg", "Description must be >= 3 !");
            return;
        }
        if(!isValidDate(date)) {
            showMessage("edit-msg", "Incorrect date!");
            return;
        }
        if(percentage<=0) {
            showMessage("edit-msg", "Percentage must be valid!");
            return;
        }
        callApi('POST', 'http://localhost:8080/api/addevaluation', function (dataReturned) {
            showMessage("edit-msg", dataReturned.message);
            if(!dataReturned.isError) {
                //add the new evaluation to the table
                var table = document.getElementById("schedule-table");
                var row = table.insertRow(table.rows.length - 2);
                row.setAttribute("id","rowid-" + dataReturned.id);
                row.insertCell(0).innerText = description;
                row.insertCell(1).innerText = getJavaDate(date);
                row.insertCell(2).innerText = percentage + '%';
                row.insertCell(3).innerHTML = '<a id="editId-' + dataReturned.id + '" href="#" hidden>Edit</a>';
                row.insertCell(4).innerHTML = '<a id="deleteId-' + dataReturned.id + '" href="#" hidden>Delete</a>';
                document.getElementById("editId-" + dataReturned.id).onclick = editSchedule;
                document.getElementById("deleteId-" + dataReturned.id).onclick = deleteSchedule;
                clearBoxes();
                updatePercentage();
            }
        }, {"date": date, "percentage": percentage, "description": description, "groupingId":groupingId, "scheduleId": 0});
    }
}

function editSchedule(e) {
    function fillBoxes(){
        var id = extractIdNumber(e.target.id);
        var evalRow = document.getElementById("rowid-" + id);
        document.getElementById("description-box").value = evalRow.cells[0].innerText;
        document.getElementById("date-box").value = getISODate(evalRow.cells[1].innerText);
        var p = evalRow.cells[2].innerText;
        document.getElementById("percentage-box").value = parseFloat(p.substr(0, p.length-1));
    }
    document.getElementById("edit-form-title").innerText = "Editing an evaluation";
    fillBoxes();
    document.getElementById("action-button").innerText = "Save";
    setUpEditForm(true);
    var scheduleId = extractIdNumber(e.target.id);
    document.getElementById("action-button").onclick = function() {
        var description = document.getElementById("description-box").value;
        var date = document.getElementById("date-box").value;
        var percentage = document.getElementById("percentage-box").value;
        var groupingId = document.getElementById("selectedPair").selectedOptions[0].value;
        if(description.length <= 2) {
            showMessage("edit-msg", "Description must be >= 3 !");
            return;
        }
        if(!isValidDate(date)) {
            showMessage("edit-msg", "Incorrect date!");
            return;
        }
        if(percentage<=0) {
            showMessage("edit-msg", "Percentage must be valid!");
            return;
        }
        callApi('PUT', 'http://localhost:8080/api/updateevaluation', function (dataReturned) {
            if(dataReturned.isError) {
                showMessage("edit-msg", dataReturned.message);
            }else{
                //update the new evaluation to the table
                var row = document.getElementById("rowid-" + scheduleId);
                row.cells[0].innerText = description;
                row.cells[1].innerText = getJavaDate(date);
                row.cells[2].innerText = percentage + '%';
                updatePercentage();
                setUpEditForm(false);
                showMessage("under-table-msg", dataReturned.message);
            }
        }, {"date": date, "percentage": percentage, "description": description, "groupingId":groupingId, "scheduleId": scheduleId});
    }
}

function deleteSchedule(e) {
    var scheduleId = extractIdNumber(e.target.id);
    console.log("scheduleId: " + scheduleId);
    callApi('DELETE', 'http://localhost:8080/api/deleteevaluation', function (dataReturned) {
        showMessage("under-table-msg", dataReturned.message);
        if(!dataReturned.isError) {
            var row = document.getElementById("rowid-" + scheduleId);
            row.outerHTML = "";
            updatePercentage();
        }
    }, {"date": "1969-11-21", "percentage": 0, "description": "none", "groupingId": 0, "scheduleId": scheduleId});
}

function editFormClose() {
    setUpEditForm(false);
}

function setUpEditForm(active) {
    grayer("groupSubjectPairForm", active);
    forEachIdTag("editId-", function(element){ element.hidden = active; })
    forEachIdTag("deleteId-", function(element){ element.hidden = active; })
    document.getElementById("a-link-add").hidden = active;
    document.getElementById("edit-form").hidden = !active;
}

function updatePercentage(){
    var rows = document.getElementById("schedule-table").rows;
    var L = rows.length;
    if(L <=3 ) return;
    var sum = 0;
    for(i = 1; i < L - 2; i++) {
        var s = rows[i].cells[2].innerText;
        sum = sum + parseFloat(s.substr(0, s.length-1));
    }
    document.getElementById('total').innerText = sum + '%';
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

function forEachIdTag(idTag, doForEach){
    //Execute a doForEach function on each "A" (link) element in the entire document which id tag starts with the string idTag
    var elementList = document.getElementsByTagName("A");
    var L = elementList.length;
    for(i = 0; i < L; i++){
        if(elementList[i].id.startsWith(idTag)) {
            doForEach(elementList[i]);
        }
    }
}

function extractIdNumber(idTag){
    var tag = idTag.split('-');
    return parseInt(tag[tag.length-1]);
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

function isValidDate(date) {
    var temp = date.split('-');
    var d = new Date(temp[0] + '/' + temp[1] + '/' + temp[2]);
    return (d && d.getMonth() + 1 == temp[1] && d.getDate() == Number(temp[2]) && d.getFullYear() == Number(temp[0]));
}

function getISODate(date) {
    //converts from 11/21/2019 to 2019-11-21
    var d = new Date(date),
        month = '' + (d.getMonth() + 1),
        day = '' + d.getDate(),
        year = d.getFullYear();

    if (month.length < 2) month = '0' + month;
    if (day.length < 2) day = '0' + day;

    return [year, month, day].join('-');
}

function getJavaDate(date) {
    //converts from 2019-11-21 to 11/21/2019
    var ymd = date.split("-");
    return [ymd[1], ymd[2], ymd[0]].join('/');
}