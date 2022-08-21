function setUserId(id){
	$(".userID").val(id);
}
function checkboxChequeChanged(){	
	if ($('#checkboxCheque').is(':checked')) {
		$('#paramsChequeTable').show();
	}else{
		$('#paramsChequeTable').hide();
	}
}
function checkboxLcnChanged(){	
	if ($('#checkboxLcn').is(':checked')) {
		$('#paramsLcnTable').show();
	}else{
		$('#paramsLcnTable').hide();
	}
}