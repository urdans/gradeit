 
document.onload = setup();

function setup() {
    forEachIdTag("groupingId-", function(element){ element.onclick = showDetails; })
}

function clearActive(e){
	if(e.target.getAttribute("class").includes("active")) return;
    //clears active value
    forEachIdTag("groupingId-", function(element){ element.setAttribute("class","text-left nav-link"); });
	e.target.setAttribute("class","text-left nav-link active");
}

function showDetails(e) {
    // setUpEditForm(false);
    document.getElementById("student-grades-form").hidden = false;
    clearActive(e);
	var groupingId = extractIdNumber(e.target.id);
	var studentId = parseInt(document.getElementById('sid').getAttribute("data-sid"));
	var subjectName = document.getElementById("groupingId-" + groupingId).innerText;
    callApi('GET', 'https://gradeit19.herokuapp.com/api/getgrades?groupingid=' + groupingId + '&studentid=' + studentId, function (dataReturned) {
        if(dataReturned) {
            var table = document.getElementById("grades-table");
            while(table.rows.length > 2) table.deleteRow(1);
            var L = dataReturned.grades.length;
            var cum = 0.0;
            var totalperc = 0;
            for(i = 0; i < L; i++){
                var row = table.insertRow(table.rows.length - 1);
                row.insertCell(0).innerText = getJavaDate(dataReturned.grades[i].date);
                row.insertCell(1).innerText = dataReturned.grades[i].description;
                var percentage = parseFloat(dataReturned.grades[i].percentage);
                row.insertCell(2).innerText =  percentage + '%';
                var gradeValue = "";
                if(dataReturned.grades[i].gradeValue != null) gradeValue = Number.parseFloat(dataReturned.grades[i].gradeValue).toFixed(1);
                var gradeId = '0';
                if(dataReturned.grades[i].gradeId != null) gradeId = dataReturned.grades[i].gradeId;
				var scheduleId = dataReturned.grades[i].scheduleId;
                row.insertCell(3).innerText = gradeValue;
                cum = cum + gradeValue*percentage*0.01;
                totalperc = totalperc + percentage;
			}
			document.getElementById("detail-subjectname").innerText = subjectName + " Evaluation Plan";
            document.getElementById("totalperc").innerText = totalperc + "%";
            document.getElementById("cum").innerText = Number.parseFloat(cum).toFixed(1);
			// setUpEditForm(true);
        }
    });
}

function setUpEditForm(active) {
    if (active) document.getElementById("student-grades-form").hidden = !active;
	if (!active) {
		var table = document.getElementById("grades-table");
		while(table.rows.length > 2) table.deleteRow(1);
	}
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


