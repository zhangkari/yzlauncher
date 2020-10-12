/**
 * Created by vken on 14-3-10.
 */
/**
 * 搜索
 * @param keyword
 * @param classId,高亮的css名称
 */

const SEARCH_SPAN_ID_PREFIX = 'search_';
const SEARCH_SPAN_NAME = 'search_span';
const ELEMENT_NODE_TYPE = 1;
const TEXT_NODE_TYPE = 3;

function string2Json(jsonString) {
	var json = eval("(" + jsonString + ")");
	return json;
}

function highlightSearch(key, classId) {
	var bodyElement = document.body;
	var childNodes = bodyElement.childNodes;
	var i = 0;
	for (i = 0; i < childNodes.length; i++) {
		var node = childNodes[i];
		var childText = node.textContent;
		var keyIndex = childText.indexOf(key);
		while (keyIndex >= 0) {
			var item = new Object();
			item.containerIndex = i;
			item.startIndex = keyIndex;
			item.endIndex = keyIndex + key.length;
			highlightOne(item, classId, SEARCH_SPAN_ID_PREFIX + i);
			keyIndex = childText.indexOf(key, keyIndex + 1);
		}
	}
}


function getSearchSpanRects() {
	var spans = document.getElementsByTagName('span');
	var length = spans.length;

	//var scrollObject = getDocumentBodyScrollObject();
	var scrollLeft = document.body.scrollLeft;
	var scrollTop = document.body.scrollTop;
	var allRects = new Array();
	for (var i = 0; i < spans.length; i++) {
		var rects = getRectArray(spans[i], scrollLeft, scrollTop);
		allRects = allRects.concat(rects);
	}
	return allRects;
}

function getRectArray(element, scrollLeft, scrollTop) {
	var rectList = element.getClientRects();
	var rectArray = new Array();
	for (var j = 0; j < rectList.length; j++) {
		var rect = rectList.item(j);
		var myRect =
		{
			'left': rect.left + scrollLeft,
			'top': rect.top + scrollTop,
			'width': rect.width,
			'height': rect.height
		};
		rectArray[j] = myRect;
	}
	return rectArray;
}

Array.prototype.peek = function () {
	return this[this.length - 1];
};

function highlightOne(item, classId, spanId) {
	if (item) {
		var range = document.createRange();
		var childNodes = document.body.childNodes;
		var container = childNodes[item.containerIndex];
		if (range && container) {
			var startOffset = item.startIndex;
			var endOffset = item.endIndex;
			setRange(range,container,startOffset,endOffset);
			createSpanForRange(range, classId, spanId);
		}
		range.detach();
	}
}


function setRange(range, node, startIndex, endIndex) {
	if (node.nodeType == ELEMENT_NODE_TYPE) {
		var children = node.childNodes;
		var i = 0;
		var child = children[i];
		while (child) {
			if (setRange(range, child, startIndex, endIndex)) {
				return;
			} else {
				startIndex -= child.textContent.length;
				endIndex -= child.textContent.length;
				i++;
				child = children[i];
			}
		}
	} else if (node.nodeType == TEXT_NODE_TYPE) {
		var isOk = false;
		if (startIndex>=0 && node.textContent.length > startIndex) {
			range.setStart(node, startIndex);
		}
		if (endIndex>=0 && node.textContent.length > endIndex) {
			isOk = true;
			range.setEnd(node, endIndex);
		}
		return isOk;
	}
}

function createSpanForRange(range, classId, spanId) {
	var span = document.createElement("span");
	span.setAttribute("class", classId);
	range.surroundContents(span);
}

function getDocumentBodyScrollObject() {
	var bodyStyle = document.defaultView.getComputedStyle(document.body);
	var bodyX = parseInt(bodyStyle.getPropertyValue('margin-left'));
	var bodyY = parseInt(bodyStyle.getPropertyValue('margin-top'));
	var bodyRects = document.body.getClientRects();
	var bodyRect = bodyRects.item(0);

	var scrollObject = new Object();
	scrollObject.left = bodyRect.left - bodyX;
	scrollObject.top = bodyRect.top - bodyY;

	return scrollObject;
}

/*


 var testSearchJsonText = '{"count":140,"array":[{"containerIndex":13,"startIndex":9,"endIndex":11,"value":"彼得"},{"containerIndex":13,"startIndex":87,"endIndex":89,"value":"彼得"},{"containerIndex":15,"startIndex":20,"endIndex":22,"value":"彼得"},{"containerIndex":17,"startIndex":58,"endIndex":60,"value":"彼得"},{"containerIndex":19,"startIndex":29,"endIndex":31,"value":"彼得"},{"containerIndex":19,"startIndex":74,"endIndex":76,"value":"彼得"},{"containerIndex":19,"startIndex":94,"endIndex":96,"value":"彼得"},{"containerIndex":19,"startIndex":112,"endIndex":114,"value":"彼得"},{"containerIndex":19,"startIndex":161,"endIndex":163,"value":"彼得"},{"containerIndex":21,"startIndex":7,"endIndex":9,"value":"彼得"},{"containerIndex":21,"startIndex":19,"endIndex":21,"value":"彼得"},{"containerIndex":23,"startIndex":65,"endIndex":67,"value":"彼得"},{"containerIndex":25,"startIndex":33,"endIndex":35,"value":"彼得"},{"containerIndex":29,"startIndex":0,"endIndex":2,"value":"彼得"},{"containerIndex":35,"startIndex":8,"endIndex":10,"value":"彼得"},{"containerIndex":43,"startIndex":12,"endIndex":14,"value":"彼得"},{"containerIndex":43,"startIndex":50,"endIndex":52,"value":"彼得"},{"containerIndex":45,"startIndex":2,"endIndex":4,"value":"彼得"},{"containerIndex":47,"startIndex":9,"endIndex":11,"value":"彼得"},{"containerIndex":49,"startIndex":99,"endIndex":101,"value":"彼得"},{"containerIndex":51,"startIndex":6,"endIndex":8,"value":"彼得"},{"containerIndex":51,"startIndex":21,"endIndex":23,"value":"彼得"},{"containerIndex":51,"startIndex":63,"endIndex":65,"value":"彼得"},{"containerIndex":53,"startIndex":0,"endIndex":2,"value":"彼得"},{"containerIndex":55,"startIndex":5,"endIndex":7,"value":"彼得"},{"containerIndex":57,"startIndex":20,"endIndex":22,"value":"彼得"},{"containerIndex":59,"startIndex":58,"endIndex":60,"value":"彼得"},{"containerIndex":61,"startIndex":29,"endIndex":31,"value":"彼得"},{"containerIndex":61,"startIndex":74,"endIndex":76,"value":"彼得"},{"containerIndex":61,"startIndex":94,"endIndex":96,"value":"彼得"},{"containerIndex":61,"startIndex":112,"endIndex":114,"value":"彼得"},{"containerIndex":61,"startIndex":161,"endIndex":163,"value":"彼得"},{"containerIndex":63,"startIndex":7,"endIndex":9,"value":"彼得"},{"containerIndex":63,"startIndex":19,"endIndex":21,"value":"彼得"},{"containerIndex":65,"startIndex":65,"endIndex":67,"value":"彼得"},{"containerIndex":67,"startIndex":33,"endIndex":35,"value":"彼得"},{"containerIndex":71,"startIndex":0,"endIndex":2,"value":"彼得"},{"containerIndex":77,"startIndex":8,"endIndex":10,"value":"彼得"},{"containerIndex":85,"startIndex":12,"endIndex":14,"value":"彼得"},{"containerIndex":85,"startIndex":50,"endIndex":52,"value":"彼得"},{"containerIndex":87,"startIndex":2,"endIndex":4,"value":"彼得"},{"containerIndex":89,"startIndex":9,"endIndex":11,"value":"彼得"},{"containerIndex":91,"startIndex":99,"endIndex":101,"value":"彼得"},{"containerIndex":93,"startIndex":6,"endIndex":8,"value":"彼得"},{"containerIndex":93,"startIndex":21,"endIndex":23,"value":"彼得"},{"containerIndex":93,"startIndex":63,"endIndex":65,"value":"彼得"},{"containerIndex":95,"startIndex":0,"endIndex":2,"value":"彼得"},{"containerIndex":97,"startIndex":5,"endIndex":7,"value":"彼得"},{"containerIndex":99,"startIndex":20,"endIndex":22,"value":"彼得"},{"containerIndex":101,"startIndex":58,"endIndex":60,"value":"彼得"},{"containerIndex":103,"startIndex":29,"endIndex":31,"value":"彼得"},{"containerIndex":103,"startIndex":74,"endIndex":76,"value":"彼得"},{"containerIndex":103,"startIndex":94,"endIndex":96,"value":"彼得"},{"containerIndex":103,"startIndex":112,"endIndex":114,"value":"彼得"},{"containerIndex":103,"startIndex":161,"endIndex":163,"value":"彼得"},{"containerIndex":105,"startIndex":7,"endIndex":9,"value":"彼得"},{"containerIndex":105,"startIndex":19,"endIndex":21,"value":"彼得"},{"containerIndex":107,"startIndex":65,"endIndex":67,"value":"彼得"},{"containerIndex":109,"startIndex":33,"endIndex":35,"value":"彼得"},{"containerIndex":113,"startIndex":0,"endIndex":2,"value":"彼得"},{"containerIndex":119,"startIndex":8,"endIndex":10,"value":"彼得"},{"containerIndex":127,"startIndex":12,"endIndex":14,"value":"彼得"},{"containerIndex":127,"startIndex":50,"endIndex":52,"value":"彼得"},{"containerIndex":129,"startIndex":2,"endIndex":4,"value":"彼得"},{"containerIndex":131,"startIndex":9,"endIndex":11,"value":"彼得"},{"containerIndex":133,"startIndex":99,"endIndex":101,"value":"彼得"},{"containerIndex":135,"startIndex":6,"endIndex":8,"value":"彼得"},{"containerIndex":135,"startIndex":21,"endIndex":23,"value":"彼得"},{"containerIndex":135,"startIndex":63,"endIndex":65,"value":"彼得"},{"containerIndex":137,"startIndex":0,"endIndex":2,"value":"彼得"},{"containerIndex":139,"startIndex":5,"endIndex":7,"value":"彼得"},{"containerIndex":141,"startIndex":20,"endIndex":22,"value":"彼得"},{"containerIndex":143,"startIndex":58,"endIndex":60,"value":"彼得"},{"containerIndex":145,"startIndex":29,"endIndex":31,"value":"彼得"},{"containerIndex":145,"startIndex":74,"endIndex":76,"value":"彼得"},{"containerIndex":145,"startIndex":94,"endIndex":96,"value":"彼得"},{"containerIndex":145,"startIndex":112,"endIndex":114,"value":"彼得"},{"containerIndex":145,"startIndex":161,"endIndex":163,"value":"彼得"},{"containerIndex":147,"startIndex":7,"endIndex":9,"value":"彼得"},{"containerIndex":147,"startIndex":19,"endIndex":21,"value":"彼得"},{"containerIndex":149,"startIndex":65,"endIndex":67,"value":"彼得"},{"containerIndex":151,"startIndex":33,"endIndex":35,"value":"彼得"},{"containerIndex":155,"startIndex":0,"endIndex":2,"value":"彼得"},{"containerIndex":161,"startIndex":8,"endIndex":10,"value":"彼得"},{"containerIndex":169,"startIndex":12,"endIndex":14,"value":"彼得"},{"containerIndex":169,"startIndex":50,"endIndex":52,"value":"彼得"},{"containerIndex":171,"startIndex":2,"endIndex":4,"value":"彼得"},{"containerIndex":173,"startIndex":9,"endIndex":11,"value":"彼得"},{"containerIndex":175,"startIndex":99,"endIndex":101,"value":"彼得"},{"containerIndex":177,"startIndex":6,"endIndex":8,"value":"彼得"},{"containerIndex":177,"startIndex":21,"endIndex":23,"value":"彼得"},{"containerIndex":177,"startIndex":63,"endIndex":65,"value":"彼得"},{"containerIndex":179,"startIndex":0,"endIndex":2,"value":"彼得"},{"containerIndex":181,"startIndex":5,"endIndex":7,"value":"彼得"},{"containerIndex":183,"startIndex":20,"endIndex":22,"value":"彼得"},{"containerIndex":185,"startIndex":58,"endIndex":60,"value":"彼得"},{"containerIndex":187,"startIndex":29,"endIndex":31,"value":"彼得"},{"containerIndex":187,"startIndex":74,"endIndex":76,"value":"彼得"},{"containerIndex":187,"startIndex":94,"endIndex":96,"value":"彼得"},{"containerIndex":187,"startIndex":112,"endIndex":114,"value":"彼得"},{"containerIndex":187,"startIndex":161,"endIndex":163,"value":"彼得"},{"containerIndex":189,"startIndex":7,"endIndex":9,"value":"彼得"},{"containerIndex":189,"startIndex":19,"endIndex":21,"value":"彼得"},{"containerIndex":191,"startIndex":65,"endIndex":67,"value":"彼得"},{"containerIndex":193,"startIndex":33,"endIndex":35,"value":"彼得"},{"containerIndex":197,"startIndex":0,"endIndex":2,"value":"彼得"},{"containerIndex":203,"startIndex":8,"endIndex":10,"value":"彼得"},{"containerIndex":211,"startIndex":12,"endIndex":14,"value":"彼得"},{"containerIndex":211,"startIndex":50,"endIndex":52,"value":"彼得"},{"containerIndex":213,"startIndex":2,"endIndex":4,"value":"彼得"},{"containerIndex":215,"startIndex":9,"endIndex":11,"value":"彼得"},{"containerIndex":217,"startIndex":99,"endIndex":101,"value":"彼得"},{"containerIndex":219,"startIndex":6,"endIndex":8,"value":"彼得"},{"containerIndex":219,"startIndex":21,"endIndex":23,"value":"彼得"},{"containerIndex":219,"startIndex":63,"endIndex":65,"value":"彼得"},{"containerIndex":221,"startIndex":0,"endIndex":2,"value":"彼得"},{"containerIndex":223,"startIndex":5,"endIndex":7,"value":"彼得"},{"containerIndex":225,"startIndex":20,"endIndex":22,"value":"彼得"},{"containerIndex":227,"startIndex":58,"endIndex":60,"value":"彼得"},{"containerIndex":229,"startIndex":29,"endIndex":31,"value":"彼得"},{"containerIndex":229,"startIndex":74,"endIndex":76,"value":"彼得"},{"containerIndex":229,"startIndex":94,"endIndex":96,"value":"彼得"},{"containerIndex":229,"startIndex":112,"endIndex":114,"value":"彼得"},{"containerIndex":229,"startIndex":161,"endIndex":163,"value":"彼得"},{"containerIndex":231,"startIndex":7,"endIndex":9,"value":"彼得"},{"containerIndex":231,"startIndex":19,"endIndex":21,"value":"彼得"},{"containerIndex":233,"startIndex":65,"endIndex":67,"value":"彼得"},{"containerIndex":235,"startIndex":33,"endIndex":35,"value":"彼得"},{"containerIndex":239,"startIndex":0,"endIndex":2,"value":"彼得"},{"containerIndex":245,"startIndex":8,"endIndex":10,"value":"彼得"},{"containerIndex":253,"startIndex":12,"endIndex":14,"value":"彼得"},{"containerIndex":253,"startIndex":50,"endIndex":52,"value":"彼得"},{"containerIndex":255,"startIndex":2,"endIndex":4,"value":"彼得"},{"containerIndex":257,"startIndex":9,"endIndex":11,"value":"彼得"},{"containerIndex":259,"startIndex":99,"endIndex":101,"value":"彼得"},{"containerIndex":261,"startIndex":6,"endIndex":8,"value":"彼得"},{"containerIndex":261,"startIndex":21,"endIndex":23,"value":"彼得"},{"containerIndex":261,"startIndex":63,"endIndex":65,"value":"彼得"},{"containerIndex":263,"startIndex":0,"endIndex":2,"value":"彼得"},{"containerIndex":265,"startIndex":5,"endIndex":7,"value":"彼得"}]}';

 function searchAndHighlight() {
 highlightAll(testSearchJsonText);
 }

 function highlightAll(json) {
 var searchResult = string2Json(json);
 if (searchResult.count <= 0)
 return false;
 var i = 0;
 for (i = 0; i < searchResult.array.length; i++) {
 var item = searchResult.array[i];
 highlightOne(item);
 }
 }

 */