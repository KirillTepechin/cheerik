var like = function(id) {
  var request = new Object();
  request.id = id;

  jQuery.ajax({
    type       : 'POST',
    url        : 'http://localhost:8080/like',
    contentType: 'application/json',
    data       : JSON.stringify(request)

  }).then((response) => {
  var elem = document.getElementById("post-"+id);
    	elem.innerHTML=' '+response.count;
    	if(response.color==="grey"){
    	    elem.style.color = "grey"
    	}
    	if(response.color==="red"){
            elem.style.color = "red";
        }
      })
};