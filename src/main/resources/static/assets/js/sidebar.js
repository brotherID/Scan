function w3_open() {
	  document.getElementById("main").style.marginLeft = "13%";
	  document.getElementById("mySidebar").style.width = "13%";
	  document.getElementById("mySidebar").style.display = "block";
	  document.getElementById("openNav").style.display = "none";
	}
function w3_close() {
  document.getElementById("main").style.marginLeft = "0%";
  document.getElementById("mySidebar").style.display = "none";
  document.getElementById("openNav").style.display = "inline-block";
  document.getElementById("openNav").innerHTML="&#9776;";
}

function w3_open_close() {
	
	var sidebar=document.getElementById("mySidebar");
	if(sidebar.style.display === "none"){
		//open
		document.getElementById("main").style.marginLeft = "13%";
		document.getElementById("mySidebar").style.width = "13%";
		document.getElementById("mySidebar").style.display = "block";
		document.getElementById("openNav").style.display = "block";
		document.getElementById("openNav").innerHTML="&times;";
	}
	else{
		//close	
		document.getElementById("main").style.marginLeft = "0%";
	  	document.getElementById("mySidebar").style.display = "none";
	  	document.getElementById("openNav").style.display = "inline-block";
		document.getElementById("openNav").innerHTML="&#9776;";
	}
	
	
	
	
	
	
	

}