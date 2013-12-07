/* Funciones carga de imagenes online */
function seekImg(image, folderName, fileName) {
	image.onerror = "";
    image.src = "/storage/"+folderName+"~"+fileName;
    image.style.display = "none";
    return true;
}

function afterLoad(image) {
	image.parentNode.parentNode.background = "none";
	image.style.display = "block";
}

/* Funcion previsualizacion portada insertar */
function showImage(idThumb, img) {
	var value = img.value;
	if (value == null || value == undefined || value == "")
		document.getElementById(idThumb).background = "none";
	else {
		var img1 = new Image();
		img1.onerror = function() { 
			document.getElementById(idThumb).style.backgroundImage = "url('/storage/listado"+"~"+value+"')";
		};
		img1.src = "/resources/images/listado/"+value;
		document.getElementById(idThumb).style.backgroundImage = "url('/resources/images/listado/"+value+"')";
	}
}