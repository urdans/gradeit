/* var nav1Children = document.getElementById("nav1").children;
for(i=0; i<nav1Children.length; i++){
	if(nav1Children[i].tagName == "A"){
		if(!nav1.children[i].getAttribute("class").includes("disabled")){
			nav1Children[i].addEventListener("click",clearActive);
		}
	}
}
 */


function clearActive(e){
	if(e.target.getAttribute("class").includes("active")){
		return;
	}
	//clears active value
	var nav1Children = document.getElementById("nav1").children;
	for(i=0; i<nav1Children.length; i++){
		if(nav1Children[i].tagName == "A"){
			if(!nav1.children[i].getAttribute("class").includes("disabled")){
				nav1Children[i].setAttribute("class","nav-link");
			}
		}
	}
	//sets active value
	e.target.setAttribute("class","nav-link active");
}

function selectSubject(e){
 	if(e.target.getAttribute("class").includes("active")){
		return; //has clicked on an active item
	}
	
	//clears active value
	for(i=1;i<15;i++){
		var el = document.getElementById("d"+i);
		if(el==null) break;
		el.setAttribute("class","nav-link");
	} 
	
	//sets active value
	e.target.setAttribute("class","nav-link active");
	var subjectid=e.target.getAttribute("data-subjectid");
	console.log(subjectid);
	//usar ajax para pedir los datos de evaluacion del alumno usando el subjectid (no es el mejor nombre).
	//
}