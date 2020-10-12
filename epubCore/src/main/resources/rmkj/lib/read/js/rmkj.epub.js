/**
 * Created by ruanmei on 13-12-28.
 */

(function($) {
	$.Epub = function() {
		/* epub 显示范围 */
		var width = 0;
		var height = 0;

		/* 页码 */
		var currentPage = 0;
		var totalPage = 0;

		var highlightClass;

		/* 横版或者竖版 */
		var orientation = 0;

		/*
		 * 私有函数：trace
		 */
		function trace(text) {
			var traceTag = "rmkj.epub.js: ";
			if (window.console && window.console.log)
				window.console.log(traceTag + text);
		}

		function postOperation(opreation, parameters) {
			client.sendMessage(opreation, parameters);
		}

		/*
		 * 客户端通信接口
		 */
		var client = null;

		this.init = function(w, h) {
			width = w;
			height = h;
			client = new $.Client();
			client.setPlatform(client.platformTypeEnum.IOS);

		};

		this.setPlatform = function(platform) {
			if (platform == "ios") {
				client.setPlatform(client.platformTypeEnum.IOS);
			} else if (platform == "android") {
				client.setPlatform(client.platformTypeEnum.ANDROID);
			} else if (platform == "windows") {
				client.setPlatform(client.platformTypeEnum.WINDOWS);
			} else {
				trace("invalid platform");
			}
		};

		this.setHighlightClass = function(cls) {
			highlightClass = cls;
		};
		this.setOrientation = function(o) {
			orientation = o;
		};

		/**
		 * 
		 */
		var loadFromPage = false;
		var loadPage = 0;
		var loadFromPercent = false;
		var loadPercent = 0;
		var loadFromAnchor = false;
		var loadAnchor;
		var loadFromEnd = false;

		var loadFromTotalAndCurrent = false;
		var loadTotal = 0;
		var loadCurrent = 0;

		function resetLoad() {
			loadFromPage = false;
			loadPage = 0;
			loadFromPercent = false;
			loadPercent = 0.0;
			loadFromAnchor = false;
			loadAnchor = "";
			loadFromEnd = false;
			loadFromTotalAndCurrent = false;
			loadTotal = 0;
			loadCurrent = 0;
		}

		/*
		 * 初始化显示位置 -1: 末尾显示 0 : 开始 n : 具体位置
		 */
		this.setStartPage = function(page) {
			resetLoad();
			var startPage = parseInt(page);
			loadFromPage = true;
			loadPage = startPage;

		};

		this.setStartFromEnd = function() {
			resetLoad();
			loadFromEnd = true;

		};

		/*
		 * 初始化显示位置百分比 -1: 末尾显示 0 : 开始 n : 具体位置
		 */
		this.setStartPercent = function(percent) {
			resetLoad();
			loadFromPercent = true;
			loadPercent = percent;

		};

		/**
		 * 设置初始显示锚点
		 */
		this.setStartAnchor = function(anchor) {
			resetLoad();
			loadFromAnchor = true;
			loadAnchor = anchor;

		};

		/**
		 * 设置从指定总数的某个页面显示，用在章节不变化的情况下
		 */
		this.setStartFromTotalAndCurrent = function(total, current) {
			loadFromTotalAndCurrent = true;
			loadTotal = total;
			loadCurrent = current;
		}

		/*
		 * 显示页码 外部调用接口
		 */
		this.showPage = function(page) {
			doShowPage(page);
			/*
			 * showPageWithoutNotify(page);
			 */
		};

		/**
		 * 显示某个锚点对应页面 外部调用接口
		 * 
		 * @param anchor
		 */
		this.showAnchor = function(anchor) {
			handleAnchor(anchor);
		}
		/**
		 * this.bodyClick = function (event) { clickBlank(event.clientX,
		 * event.clientY); };
		 */
		$(document).click(function(event) {
			clickBlank(event.clientX, event.clientY);
		});

		/*
		 * 添加事件响应
		 */
		function addClickHandler() {

			$('a').click(
					function() {
						var href = $(this).attr("href");
						var anchorIndex = href.indexOf("#");
						if (anchorIndex == 0) {
							var anchorName = href.substring(anchorIndex + 1,
									href.length);
							handleAnchor(anchorName);
						} else {
							clickHref(href);
						}
						stopEvent($(this));
						return false;
					});

/*			$('img').click(function() {
				var className = $(this).attr("class");
				var name = $(this).attr("name");
				if (className == 'note_tag') {
					editNote(name);
				}else if(className == "annotation")
				{
					var text = $(this).attr("title");
					clickAnnotation(text);
				}else {
					var src = this.src;
					clickImage(src);
				}
				stopEvent($(this));
				return false;
			});*/

/*			$('span').click(function() {
				var spanClass = $(this).attr("class");
				if (spanClass == highlightClass) {
					var id = $(this).attr("id");
					var offsetTop = this.offsetTop;
					var offsetTopInt = parseInt(offsetTop);
					var yInPage = offsetTopInt % height;

					clickNote(id, yInPage);
				}
				stopEvent($(this));
				return false;
			});*/

			$(".hightlight").bind("click",function(){
				var spanClass = $(this).attr("class");
				if (spanClass == highlightClass) {
					var id = $(this).attr("id");
					var offsetTop = this.offsetTop;
					var offsetTopInt = parseInt(offsetTop);
					var yInPage = offsetTopInt % height;

					clickNote(id, yInPage);
				}
				stopEvent($(this));
				return false;
			});
		}

		this.handleClickEvent = function(x, y) {
			var ele = document.elementFromPoint(x, y);
			if (ele == null) {
				clickBlank(x, y);
				return true;
			}
			// 判断其他标签处理
			var name = "" + ele.tagName.toLowerCase();
			if (name == "img" || name == "a") {
				return;
			} else if (name == "span") {
				var spanClass = ele.getAttribute("class");
				if (spanClass == highlightClass) {
					return;
				}
			} else {
				clickBlank(x, y);
			}
		};

		function stopEvent(e) {
			if (e && e.stopPropagation) {
				e.stopPropagation();
			} else {
				window.event.cancelBubble = true;
			}
		}

		/*
		 * 锚跳转处理，计算锚点的位置，通知给android，进行跳转
		 * 返回json{"anchor":"id",position:{"x":"","y":100}}
		 */
		function handleAnchor(anchor) {
			showAnchorWithoutNotify(anchor)
			gotoAnchor(anchor);
		}

		function showAnchorWithoutNotify(anchor) {
			var element = document.getElementById(anchor);
			if (!element)
				return;

			var pos = findElementPosition(element);
			var pageA = pos.y / height;
			var pageB = parseInt(pageA);
			showPageWithoutNotify(pageB);
		}

		function doShowPage(page) {

			showPageWithoutNotify(page);
			pageChanged();
		}

		function isVertical() {
			if (orientation == 0)
				return false;
			else
				return true;
		}

		function showPageWithoutNotify(page) {
			currentPage = page;
			if (isVertical() == false) {
				var scrollPos = currentPage * width;
				scrollPos = parseInt(scrollPos);
				/*
				 * 不知道什么用docuemntElement不行 document.documentElement.scrollLeft =
				 * scrollPos;
				 */
				document.body.scrollLeft = scrollPos;
			} else {
				var scrollPos = currentPage * height;
				scrollPos = parseInt(scrollPos);
				/*
				 * 不知道什么用docuemntElement不行 document.documentElement.scrollLeft =
				 * scrollPos;
				 */
				document.body.scrollTop = scrollPos;
			}
		}

		function findElementPosition(oElement) {
			var w = oElement.offsetWidth;
			var h = oElement.offsetHeight;

			if (typeof (oElement.offsetParent) != 'undefined') {
				for ( var posX = 0, posY = 0; oElement; oElement = oElement.offsetParent) {
					posX += oElement.offsetLeft;
					posY += oElement.offsetTop;
				}
				return {
					"x" : posX,
					"y" : posY,
					"w" : w,
					"h" : h
				};
			} else {
				return {
					"x" : oElement.x,
					"y" : oElement.y,
					"w" : w,
					"h" : h
				}
			}
		}

		/**
		 * 设置字体颜色
		 * 
		 * @param color
		 */
		this.setTextColor = function(c) {
			$("body").css("color", c);// 换字体的颜色
			$("div").css("color", c);// 换字体的颜色
			$("p").css("color", c);// 换字体的颜色
		}

		/** **************************************** */
		var startPoint;
		var endPoint;

		this.clearSelection = function()
		{
			startPoint={'x':0,'y':0};
			endPoint={'x':0,'y':0};
			document.getSelection().removeAllRanges();
			window.getSelection().removeAllRanges();
		}

		/**
		 * 设置选中起始位置
		 */
		this.setStartPos = function(x, y) {
			startPoint = {
				'x' : parseFloat(x + ""),
				'y' : parseFloat(y + "")
			};
		};

		/**
		 * 设置选中结束位置
		 */
		function setEndPos(x, y) {
			endPoint = {
				'x' : parseFloat(x + ""),
				'y' : parseFloat(y + "")
			};
		}

		/**
		 * 移动选中到心得位置
		 */
		this.moveSelectionTo = function(x, y) {
			setEndPos(x, y);
			updateSelection();
		};

		/**
		 * 刷新选中区域
		 */
		function updateSelection() {
			var startRange = document.caretRangeFromPoint(startPoint.x,
					startPoint.y);
			startRange.expand("word");

			var endRange = document.caretRangeFromPoint(endPoint.x, endPoint.y);
			endRange.expand("word");

			var range = document.createRange();
			range.setStart(startRange.startContainer, startRange.startOffset);
			range.setEnd(endRange.startContainer, endRange.startOffset);

			var sel = document.getSelection();
			sel.removeAllRanges();
			sel.addRange(range);
		}

		// 生成唯一编码
		function guidGenerator() {
			var S4 = function() {
				return (((1 + Math.random()) * 0x10000) | 0).toString(16)
						.substring(1);
			};
			var str = (S4() + S4() + S4() + S4() + S4() + S4() + S4() + S4());
			return str;
		}

        function addEndTag(html)
        {
            var ret = html.replace(/">/g,"\" />");
            return ret;
        }

		/**
		 * 获取选中高亮文本
		 */
//		this.getHighlightSelectionText = function() {
//			var selection = document.getSelection();
//			var range = selection.getRangeAt(0);
//
//			var selectionText = selection.toString();
//			// selection.removeAllRanges ();
//
//			var highlightArray = new Array();
//			var hlID = guidGenerator();
//			// var selectionText = selection.toString();
//			// 只有一个段落的情况
//			if (range.startContainer.parentElement == range.endContainer.parentElement) {
//				var firstElement = range.startContainer.parentElement;
//				var firstHtml = firstElement.outerHTML.toString();
//                //添加末尾标签
//                firstHtml = addEndTag(firstHtml);
//
//				var firstSelection = selectionText;
//				var firstSpannedText = "<span id=\"" + hlID + "\" class=\""
//						+ highlightClass + "\" name=\"" + hlID + "\">"
//						+ firstSelection + "</span>";
//				var firstReplaceHtml = firstHtml.replace(firstSelection,
//						firstSpannedText);
//
//				var json = {
//					"srcSelection" : firstSelection,
//					"replaceHtml" : firstReplaceHtml,
//					"srcHtml" : firstHtml,
//					"isEnd" : true
//				};
//				highlightArray.push(json);
//			} else {
//				// 多段落的情况
//				// 第一段
//				var firstElement = range.startContainer.parentElement;
//				var firstHtml = firstElement.outerHTML.toString();
//                //添加末尾标签
//                firstHtml = addEndTag(firstHtml);
//
//				var firstSelection = firstElement.innerHTML.toString().substr(
//						range.startOffset);
//				var firstSpannedText = "<span id=\"" + hlID + "\" class=\""
//						+ highlightClass + "\" name=\"" + hlID + "\">"
//						+ firstSelection + "</span>";
//				var firstReplaceHtml = firstHtml.replace(firstSelection,
//						firstSpannedText);
//				var json = {
//					"srcSelection" : firstSelection,
//					"replaceHtml" : firstReplaceHtml,
//					"srcHtml" : firstHtml,
//					"isEnd" : false
//				};
//				highlightArray.push(json);
//
//				// 最后一段
//				var lastElement = range.endContainer.parentElement;
//				var lastHtml = lastElement.outerHTML.toString();
//                //添加末尾标签
//                lastHtml = addEndTag(lastHtml);
//
//
//				var lastSelection = lastElement.innerHTML.toString().substr(0,
//						range.endOffset);
//				var lastSpannedText = "<span id=\"" + hlID + "\" class=\""
//						+ highlightClass + "\" name=\"" + hlID + "\">"
//						+ lastSelection + "</span>";
//				var lastReplaceHtml = lastHtml.replace(lastSelection,
//						lastSpannedText);
//				json = {
//					"srcSelection" : lastSelection,
//					"replaceHtml" : lastReplaceHtml,
//					"srcHtml" : lastHtml,
//					"isEnd" : true
//				};
//				highlightArray.push(json);
//
//				// 其他段落
//				var middleElement = firstElement.nextElementSibling;
//				while (middleElement && middleElement != lastElement) {
//					var middleHtml = middleElement.outerHTML.toString();
//                    //添加末尾标签
//                    middleHtml = addEndTag(middleHtml);
//
//
//					var middleHtmlSelection = middleElement.innerHTML
//							.toString();
//					var middleSpannedText = "<span id=\"" + hlID
//							+ "\" class=\"" + highlightClass + "\" name=\""
//							+ hlID + "\">" + middleHtmlSelection + "</span>";
//					var middleReplaceHtml = middleHtml.replace(
//							middleHtmlSelection, middleSpannedText);
//					json = {
//						"srcSelection" : middleHtmlSelection,
//						"replaceHtml" : middleReplaceHtml,
//						"srcHtml" : middleHtml,
//						"isEnd" : false
//					};
//					highlightArray.push(json);
//
//					// 下一个段落
//					middleElement = middleElement.nextElementSibling;
//				}
//			}
//
//			// selection.addRange(range);
//
//			var json = {
//				"selectionReplaceArray" : highlightArray,
//				"selectionText" : selectionText,
//				"id" : hlID
//			};
//			postOperation("getHighlightSelectionText", json);
//		};



        this.getHighlightSelectionText = function() {
            var selection = document.getSelection();
            var range = selection.getRangeAt(0);

            var selectionText = selection.toString();
            // selection.removeAllRanges ();

            var highlightArray = new Array();
            var hlID = guidGenerator();
            // var selectionText = selection.toString();
            // 只有一个段落的情况
            if (range.startContainer.parentElement == range.endContainer.parentElement) {
                var firstElement = range.startContainer.parentElement;
                var firstHtml = firstElement.outerHTML.toString();
                //添加末尾标签
                firstHtml = addEndTag(firstHtml);

                var firstSelection = selectionText;
                var firstSpannedText = "<span id=\"" + hlID + "\" class=\""
                    + highlightClass + "\" name=\"" + hlID + "\">"
                    + firstSelection + "</span>";
                var firstReplaceHtml = firstHtml.replace(firstSelection,
                    firstSpannedText);

                var json = {
                    "srcSelection" : firstSelection,
                    "replaceHtml" : firstReplaceHtml,
                    "srcHtml" : firstHtml,
                    "isEnd" : true
                };
                highlightArray.push(json);
            } else {
                // 多段落的情况
                // 第一段
                var firstElement = range.startContainer.parentElement;
                var firstHtml = firstElement.outerHTML.toString();
                //添加末尾标签
                firstHtml = addEndTag(firstHtml);

                var firstSelection = firstElement.innerHTML.toString().substr(
                    range.startOffset);
                var firstSpannedText = "<span id=\"" + hlID + "\" class=\""
                    + highlightClass + "\" name=\"" + hlID + "\">"
                    + firstSelection + "</span>";
                var firstReplaceHtml = firstHtml.replace(firstSelection,
                    firstSpannedText);
                var json = {
                    "srcSelection" : firstSelection,
                    "replaceHtml" : firstReplaceHtml,
                    "srcHtml" : firstHtml,
                    "isEnd" : false
                };
                highlightArray.push(json);

                // 最后一段
                var lastElement = range.endContainer.parentElement;
                var lastHtml = lastElement.outerHTML.toString();
                //添加末尾标签
                lastHtml = addEndTag(lastHtml);


                var lastSelection = lastElement.innerHTML.toString().substr(0,
                    range.endOffset);
                var lastSpannedText = "<span id=\"" + hlID + "\" class=\""
                    + highlightClass + "\" name=\"" + hlID + "\">"
                    + lastSelection + "</span>";
                var lastReplaceHtml = lastHtml.replace(lastSelection,
                    lastSpannedText);
                json = {
                    "srcSelection" : lastSelection,
                    "replaceHtml" : lastReplaceHtml,
                    "srcHtml" : lastHtml,
                    "isEnd" : true
                };
                highlightArray.push(json);

                // 其他段落
                var middleElement = firstElement.nextElementSibling;
                while (middleElement && middleElement != lastElement) {
                    var middleHtml = middleElement.outerHTML.toString();
                    //添加末尾标签
                    middleHtml = addEndTag(middleHtml);


                    var middleHtmlSelection = middleElement.innerHTML
                        .toString();
                    var middleSpannedText = "<span id=\"" + hlID
                        + "\" class=\"" + highlightClass + "\" name=\""
                        + hlID + "\">" + middleHtmlSelection + "</span>";
                    var middleReplaceHtml = middleHtml.replace(
                        middleHtmlSelection, middleSpannedText);
                    json = {
                        "srcSelection" : middleHtmlSelection,
                        "replaceHtml" : middleReplaceHtml,
                        "srcHtml" : middleHtml,
                        "isEnd" : false
                    };
                    highlightArray.push(json);

                    // 下一个段落
                    middleElement = middleElement.nextElementSibling;
                }
            }

            // selection.addRange(range);

            var json = {
                "selectionReplaceArray" : highlightArray,
                "selectionText" : selectionText,
                "id" : hlID
            };
            postOperation("getHighlightSelectionText", json);
        };

		/**
		 * 删除下划线
		 */
		this.deleteSpan = function(eleName) {
			var eleArr = document.getElementsByName(eleName);
			if (eleArr.length > 0) {
				for ( var i = eleArr.length - 1; i >= 0; i--) {
					var node = eleArr[i];
					node.parentNode.insertBefore(document
							.createTextNode(node.innerHTML), node);
					node.parentNode.removeChild(node);
				}
			}
		}

		/**
		 * 获取高亮文本
		 */
		this.getSelectionText = function() {
			getSelection();
		}

		/**
		 * 获取高亮文本
		 */
		this.getBookMarkDesc = function() {
			getMarkDesc();
		}

		/** **************************************回掉外部接口********************************************** */
		/*
		 * 加载完成 回掉外部接口
		 */
		this.loadComplete = function() {
			/*
			 * 添加响应事件
			 */
			addClickHandler();

			if (isVertical() == false) {
				/* 获取页码总数 */
				var scrollWidth = document.documentElement.scrollWidth;
				var totalPageFloat = (scrollWidth + width - 1) / width;
				totalPage = parseInt(totalPageFloat);
			} else {
				/* 获取页码总数 */
				var scrollHeight = document.documentElement.scrollHeight;
				var totalPageFloat = (scrollHeight + height - 1) / height;
				totalPage = parseInt(totalPageFloat);
			}

			/* 重置滚动位置 */

			document.body.scrollLeft = 0;
			document.body.scrollTop = 0;
			currentPage = 0;

			if (loadFromEnd == true) {
				showPageWithoutNotify(totalPage - 1);
			} else if (loadFromPage == true) {
				showPageWithoutNotify(loadPage);
			} else if (loadFromAnchor == true) {
				showAnchorWithoutNotify(loadAnchor);
			} else if (loadFromPercent == true) {
				var pageFloat = loadPercent * totalPage;
				pageFloat = (pageFloat * 10 + 5) / 10;
				var page = parseInt(pageFloat);
				showPageWithoutNotify(page);
			} else if (loadFromTotalAndCurrent == true) {
				if (loadTotal == 0) {
					showPageWithoutNotify(0);
				} else if (totalPage == loadTotal) {
					showPageWithoutNotify(loadCurrent);
				} else {
					var pageFloat = loadCurrent * totalPage / loadTotal;
					var page = parseInt(pageFloat);
					showPageWithoutNotify(page);
				}
			}
			var json = {
				"scrollWidth" : scrollWidth,
				"viewWidth" : width,
				"totalPage" : totalPage,
				"currentPage" : currentPage
			};
			postOperation("loadComplete", json);
		};
		/*
		 * 加载完成 回掉外部接口
		 */
		function pageChanged() {
			var json = {
				"currentPage" : currentPage,
				"totalPage" : totalPage
			};
			postOperation("pageChanged", json);
		}

		/**
		 * 跳转到某个锚点
		 * 
		 * @param anchor
		 */
		function gotoAnchor(anchor) {
			var json = {
				"anchor" : anchor,
				"currentPage" : currentPage,
				"totalPage" : totalPage
			};
			postOperation("gotoAnchor", json);
		}

		/*
		 * 点击图片 回掉外部接口
		 */
		function clickImage(src) {
			var json = {
				"src" : src + ""
			};
			postOperation("clickImage", json);
		}
		/*
		 * 点击笔记图片 回掉外部接口
		 */
		function editNote(noteId) {
			var json = {
				"id" : noteId
			};
			postOperation("editNote", json);
		}

		/*
		 * 点击新注释
		 */
		function clickAnnotation(text)
		{
			var json = {
				"annotation" : text
			};
			postOperation("clickAnnotation", json);
		}

		/*
		 * 点击连接
		 * 
		 * 回掉外部接口
		 */
		function clickHref(href) {
			var json = {
				"href" : href + ""
			};
			postOperation("clickHref", json);
		}

		/*
		 * 点击笔记 回掉外部接口
		 */
		function clickNote(id, offsetTop) {
			var json = {
				"id" : id,
				"offsetTop" : offsetTop
			};
			postOperation("clickNote", json);
		}

		/*
		 * 点击空白 回掉外部接口
		 */
		function clickBlank(x, y) {
			var json = {
				"x" : x,
				"y" : y
			};
			postOperation("clickBlank", json);
		}

		/*
		 * 选中变化
		 * 
		 * 回掉外部接口
		 */
		function getSelection() {
			var txt = document.getSelection().toString();
			var json = {
				"selectionText" : txt
			};
			postOperation("getSelection", json);
		}

		function getMarkDesc() {
			startPoint = {
				'x' : parseFloat(0 + ""),
				'y' : parseFloat(0 + "")
			};
			endPoint = {
				'x' : parseFloat(0 + ""),
				'y' : parseFloat(height-10 + "")
			};

			var startRange = document.caretRangeFromPoint(startPoint.x,
					startPoint.y);
			startRange.expand("word");

			var endRange = document.caretRangeFromPoint(endPoint.x, endPoint.y);
			endRange.expand("word");

			var range = document.createRange();
			range.setStart(startRange.startContainer, startRange.startOffset);
			range.setEnd(endRange.startContainer, endRange.startOffset);

			var text = document.getSelection();
			text.removeAllRanges();
			text.addRange(range);

			var markDesc = text.toString();

			text.removeAllRanges();

			var json = {
				"markDesc" : markDesc + ""
			};
			postOperation("getBookMarkDesc", json);
		}
		/**
         * 搜索关键词
         * @param key
         * @param classId
         * @returns {Array}
         */
        this.searchKey= function(key, classId) {
        	highlightSearch(key, classId);
            //alert("key:"+key+"\r class:"+classId);
            var bodyElement = document.body;
            var searchResult = new Array();
            searchElement(searchResult,bodyElement,key,classId);
            var json = {"searchResult": searchResult};
            postOperation("searchKey", json);
        }

        /**
         * 递归搜索关键词,结果存储在resultArray 里面
         * @param resultArray
         * @param element
         * @param key
         * @param classId
         */
        function searchElement(resultArray,element,key,classId)
        {
            var child = element.firstElementChild;
            //如果有字节点，进行递归
            if(child)
            {
                while(child)
                {
                    searchElement(resultArray,child,key,classId);
                    child = child.nextElementSibling;
                }
            }else
            {
                var tagName = element.tagName.toLowerCase();
                if(tagName == "span" && element.className==classId)
                {
                    var pos = getElementPositionInPage(element);
                    resultArray.push(pos);
                }
            }
        }

        /**
         * 获取元素位置，返回josn{x: ,y:,w:,h:}
         * @param oElement
         * @returns {*}
         */
        function getElementPositionInPage(oElement) {
            var w = oElement.offsetWidth;
            var h = oElement.offsetHeight;
            var x = 0;
            var y = 0;

            if (typeof( oElement.offsetParent ) != 'undefined') {
                for (var posX = 0, posY = 0; oElement; oElement = oElement.offsetParent) {
                    posX += oElement.offsetLeft;
                    posY += oElement.offsetTop;
                }
                x = posX;
                y = posY;
            }
            else {
                x = oElement.x;
                y = oElement.y;
            }
            var pageA = y / height;
            var pageB = parseInt(pageA);
            return {"page":pageB,"rect":{"x": x, "y": y, "w": w, "h": h}}
        }
	}

})(jQuery);
