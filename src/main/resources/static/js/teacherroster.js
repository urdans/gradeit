document.onload = setup();


function setup() {
    forEachIdTag("studentId-", function(element){ element.onclick = editEvaluations; })
    document.getElementById('save-button').onclick = saveGrades;
    document.getElementById('close-button').onclick = closeForm;
    document.getElementById('selectedPair').onchange = updatePage;
    setUpTitle();
}

function clearActive(e){
	if(e.target.getAttribute("class").includes("active")) return;
    //clears active value
    forEachIdTag("studentId-", function(element){ element.setAttribute("class","text-left nav-link"); });
	e.target.setAttribute("class","text-left nav-link active");
}

function updatePage(e) {
    document.getElementById("groupSubjectPairForm").submit();
}

function setUpTitle() {
    var s = document.getElementById("selectedPair").selectedOptions[0].innerText;
    var s1 = String(s).split(" - ");
    document.getElementById("roster-title").innerText = s1[0] + " roster for " + s1[1];
}

function editEvaluations(e) {
    clearActive(e);
    var groupingId = document.getElementById("selectedPair").selectedOptions[0].value;
    var studentId = extractIdNumber(e.target.id);
    callApi('GET', 'http://localhost:8080/api/getgrades?groupingid=' + groupingId + '&studentid=' + studentId, function (dataReturned) {
        if(dataReturned) {
            var table = document.getElementById("grades-table");
            var L = dataReturned.grades.length;
            var cum = 0.0;
            var totalperc = 0;
            for(i = 0; i < L; i++){
                var row = table.insertRow(table.rows.length - 1);
                row.insertCell(0).innerText = getJavaDate(dataReturned.grades[i].date);
                row.insertCell(1).innerText = dataReturned.grades[i].description;
                var percentage = parseFloat(dataReturned.grades[i].percentage);
                var cell = row.insertCell(2);
                cell.setAttribute("class", "text-right");
                cell.innerText =  percentage + '%';

                var gradeValue = "";
                if(dataReturned.grades[i].gradeValue != null) gradeValue = parseFloat(dataReturned.grades[i].gradeValue);
                var gradeId = '0';
                if(dataReturned.grades[i].gradeId != null) gradeId = dataReturned.grades[i].gradeId;
                var scheduleId = dataReturned.grades[i].scheduleId;
                row.insertCell(3).innerHTML = '<input id="gradeId-' + gradeId + '" type="number" class="form-control cellw text-center" value="' + gradeValue + '" data-scheduleId="' + scheduleId + '"></input>';
                cum = cum + gradeValue*percentage*0.01;
                totalperc = totalperc + percentage;
            }
            document.getElementById("totalperc").innerText = totalperc + "%";
            document.getElementById("cum").innerText = cum;
            var title = document.getElementById("student-grades-form-title");
            title.setAttribute("data-studentId", studentId);
            title.innerText = "Grades for student: " + document.getElementById('studentId-' + studentId).innerText;
            forEachIdTag("gradeId-", function(element){ element.onchange = updateCumulative; }, "INPUT");
            setUpEditForm(true);
        }
    });
}

function updateCumulative(e){
    var rows = document.getElementById("grades-table").rows;
    var L = rows.length;
    var L = rows.length;
    cum = 0;
    for(i = 1; i < L - 1; i++){
        var percentage = parseFloat(rows[i].cells[2].innerText);
        var input = rows[i].cells[3].getElementsByTagName("INPUT")[0];
        var gradeValue = 0;
        if(input.value == "") gradeValue = 0.0;
        else gradeValue = parseFloat(input.value);
            
        cum = cum + gradeValue*percentage*0.01;
    }
    document.getElementById("cum").innerText = cum;
}

function saveGrades() {
    var studentId = parseInt(document.getElementById("student-grades-form-title").getAttribute("data-studentId"));
    var rows = document.getElementById("grades-table").rows;
    var L = rows.length;
    var gradeList = [];
    for(i = 1; i < L - 1; i++){
        var input = rows[i].cells[3].getElementsByTagName("INPUT")[0];
        var gradeValue = input.value;//needed to be string
        var gradeId = extractIdNumber(input.getAttribute("Id"));//needed to be string
        var scheduleId = parseInt(input.getAttribute("data-scheduleId"));
        console.log(gradeValue + "\t\t\t" + gradeId);
        var studentGrade = {"gradeValue":gradeValue, "gradeId":gradeId, "scheduleId":scheduleId};
        gradeList.push(studentGrade);
    }
    var studentGrades = {"studentId":studentId, "gradeList":gradeList}
    callApi('POST', 'http://localhost:8080/api/setgrades', function (dataReturned) {
        showMessage("student-grades-form-msg", dataReturned.message);
        if(!dataReturned.isError) {
            location.reload();
        }
    }, studentGrades);
}

function closeForm() {
    setUpEditForm(false);
    var table = document.getElementById("grades-table");
    while(table.rows.length > 2) table.deleteRow(1);
    forEachIdTag("studentId-", function(element){ element.setAttribute("class","text-left nav-link"); });
}

function setUpEditForm(active) {
    grayer("groupSubjectPairForm", active);
    forEachIdTag("studentId-", function(element){
        if(active){
            element.onclick = null;
            element.removeAttribute("href");
        }else{
            element.onclick = editEvaluations;
            element.setAttribute("href","#");
        }
    })
    document.getElementById("student-grades-form").hidden = !active;
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

function forEachIdTag(idTag, doForEach, tag="A"){
    //Execute a doForEach function on each "A" (link) element in the entire document which id tag starts with the string idTag
    var elementList = document.getElementsByTagName(tag);
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

function getJavaDate(date) {
    //converts from 2019-11-21 to 11/21/2019
    var ymd = date.split("-");
    return [ymd[1], ymd[2], ymd[0]].join('/');
}


