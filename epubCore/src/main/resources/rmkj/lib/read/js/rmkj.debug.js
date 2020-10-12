var txt;
onerror = function(msg,url,l)
{
	txt="";
	txt+="Error:"+msg +"\n";
	txt+="URL:"+url +"\n";
	txt+="line:"+l +"\n";
	alert(txt);
	return true;
};