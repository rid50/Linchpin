            <SCRIPT type="text/javascript">
                  function f(e){
                     if (e.className=="ci") {
                       if (e.children(0).innerText.indexOf("\n")>0) fix(e,"cb");
                     }
                     if (e.className=="di") {
                       if (e.children(0).innerText.indexOf("\n")>0) fix(e,"db");
                     } e.id="";
                  }
                  function fix(e,cl){
                    e.className=cl;
                    e.style.display="block";
                    j=e.parentElement.children(0);
                    j.className="c";
                    k=j.children(0);
                    k.style.visibility="visible";
                    k.href="#";
                  }
                  function ch(e) {
                    mark=e.children(0).children(0);
                    if (mark.innerText=="+") {
                      mark.innerText="-";
                      for (var i=1;i<e.children.length;i++) {
                        e.children(i).style.display="block";
                      }
                    }
                    else if (mark.innerText=="-") {
                      mark.innerText="+";
                      for (var i=1;i<e.children.length;i++) {
                        e.children(i).style.display="none";
                      }
                    }
                  }
                  function ch2(e) {
                    mark=e.children(0).children(0);
                    contents=e.children(1);
                    if (mark.innerText=="+") {
                      mark.innerText="-";
                      if (contents.className=="db"||contents.className=="cb") {
                        contents.style.display="block";
                      }
                      else {
                        contents.style.display="inline";
                      }
                    }
                    else if (mark.innerText=="-") {
                      mark.innerText="+";
                      contents.style.display="none";
                    }
                  }
                  function cl() {
                    e=window.event.srcElement;
                    if (e.className!="c") {
                      e=e.parentElement;
                      if (e.className!="c") {
                        return;
                      }
                    }
                    e=e.parentElement;
                    if (e.className=="e") {
                      ch(e);
                    }
                    if (e.className=="k") {
                      ch2(e);
                    }
                  }
                  function ex(){}
                  function h(){window.status=" ";}
                  document.onclick=cl;
            </SCRIPT>
            <STYLE>
              BODY {font:x-small 'Verdana'; margin-right:1.5em}
                .c  {cursor:hand}
                .b  {color:red; font-family:'Courier New'; font-weight:bold;
                     text-decoration:none}
                .e  {margin-left:1em; text-indent:-1em; margin-right:1em}
                .k  {margin-left:1em; text-indent:-1em; margin-right:1em}
                .t  {color:#990000}
                .xt {color:#990099}
                .ns {color:red}
                .dt {color:green}
                .m  {color:blue}
                .tx {font-weight:bold}
                .db {text-indent:0px; margin-left:1em; margin-top:0px;
                     margin-bottom:0px;padding-left:.3em;
                     border-left:1px solid #CCCCCC; font:small Courier}
                .di {font:small Courier}
                .d  {color:blue}
                .pi {color:blue}
                .cb {text-indent:0px; margin-left:1em; margin-top:0px;
                     margin-bottom:0px;padding-left:.3em; font:small Courier;
                     color:#888888}
                .ci {font:small Courier; color:#888888}
                PRE {margin:0px; display:inline}
           </STYLE>
           
	<SCRIPT type="text/javascript">
		function initCSV() {
			if ($("myCSV") == null)
   				return;

   			if (window.ActiveXObject) {
				if (typeof $("SpreadsheetWC").XMLData == "undefined") {
					$("SpreadsheetWC").style.display="none";
				
					makeMicrosoftTransformation(myCSV, "<c:url value='/xml/'/>", "report.xml", "reportCSV.xsl")
					
/*					
        			var xmlDoc = new ActiveXObject("Msxml2.DOMDocument.3.0");
        			xmlDoc.async = false;
        			xmlDoc.load("/authoritymanager/xml/report.xml");
					if (xmlDoc.parseError.errorCode != 0)
						alert(xmlDoc.parseError);
					
        			// load XSLT stylesheet document
        			var xslDoc = new ActiveXObject("Msxml2.FreeThreadedDOMDocument.3.0");
        			xslDoc.async = false;
        			xslDoc.load("/authoritymanager/xml/reportCSV.xsl");
					if (xslDoc.parseError.errorCode != 0)
						alert(xslDoc.parseError);

					// transform the source using the XSLT stylesheet
        			myCSV.innerHTML = xmlDoc.transformNode(xslDoc);
*/        			
				} else
					$("SpreadsheetWC").style.border="solid red 2px";
			} else if (window.XSLTProcessor) {
				$("SpreadsheetWC").style.display="none";

				makeFireFoxTransformation($("myCSV"), "<c:url value='/xml/'/>", "report.xml", "reportCSV.xsl");
/*
				var XSLT = new XSLTProcessor;

  				var xmlDoc = new XMLHttpRequest;
  				xmlDoc.open('GET', '/authoritymanager/xml/report.xml', false);
  				xmlDoc.overrideMimeType('text/xml');
  				xmlDoc.send(null);
  				var xml = xmlDoc.responseXML;

  				var xslDoc = new XMLHttpRequest;
  				xslDoc.open('GET', '/authoritymanager/xml/reportCSV.xsl', false);
  				xslDoc.overrideMimeType('text/xml');
  				xslDoc.send(null);
  				var xsl = xslDoc.responseXML;

				XSLT.importStylesheet(xsl);

  				document.getElementById("myCSV").appendChild(XSLT.transformToFragment(xml, document));
*/  				
			}
		}
	
		function initXML() {
   			if ($("myXML") == null)
   				return;
   			
   			if (window.ActiveXObject) {
				makeMicrosoftTransformation(myXML, "<c:url value='/xml/'/>", "report.xml", "reportXML.xsl")
/*   			
        		// load XML document
        		var xmlDoc = new ActiveXObject("Msxml2.DOMDocument.3.0");
        		xmlDoc.async = false;
        		xmlDoc.load("/authoritymanager/xml/report.xml");
				if (xmlDoc.parseError.errorCode != 0)
					alert(xmlDoc.parseError);
					
        		// load XSLT stylesheet document
        		var xslDoc = new ActiveXObject("Msxml2.FreeThreadedDOMDocument.3.0");
        		xslDoc.async = false;
//        		xslDoc.load("res://msxml.dll/DEFAULTSS.XSL");
        		xslDoc.load("/authoritymanager/xml/reportXML.xsl");
				if (xslDoc.parseError.errorCode != 0)
					alert(xslDoc.parseError);

				// transform the source using the XSLT stylesheet
//        		myXML.innerHTML = source.transformNode(stylesheet);
				document.getElementById("myXML").insertAdjacentHTML('beforeEnd', 
										xmlDoc.transformNode(xslDoc));
*/										
/*
				// XSL Transformation
				var xslTpl = new ActiveXObject("Msxml2.XSLTemplate.3.0");
				xslTpl.stylesheet = xslDoc;
				var xslProc = xslTpl.createProcessor();
				xslProc.input = xmlDoc;

				// Transform
				xslProc.transform;
        		myXML.innerHTML = xslProc.output;
*/
				
			} else if (window.XSLTProcessor) {
				makeFireFoxTransformation($("myXML"), "<c:url value='/xml/'/>", "report.xml", "reportXML.xsl");
/*			
				var XSLT = new XSLTProcessor;

  				var xmlDoc = new XMLHttpRequest;
  				xmlDoc.open('GET', '/authoritymanager/xml/report.xml', false);
  				xmlDoc.overrideMimeType('text/xml');
  				xmlDoc.send(null);
  				var xml = xmlDoc.responseXML;

  				var xslDoc = new XMLHttpRequest;
  				xslDoc.open('GET', '/authoritymanager/xml/reportXML.xsl', false);
  				xslDoc.overrideMimeType('text/xml');
  				xslDoc.send(null);
  				var xsl = xslDoc.responseXML;

				XSLT.importStylesheet(xsl);

  				document.getElementById("myXML").appendChild(XSLT.transformToFragment(xml, document));
*/  				
			}
      	}
      	
      	function makeMicrosoftTransformation(div, path, xmlFile, xslFile) {
       		// load XML document
			var xmlDoc = new ActiveXObject("Msxml2.DOMDocument.3.0");
        	xmlDoc.async = false;
        	xmlDoc.load(path + xmlFile);
			if (xmlDoc.parseError.errorCode != 0)
				alert(xmlDoc.parseError);
					
        	// load XSLT stylesheet document
        	var xslDoc = new ActiveXObject("Msxml2.FreeThreadedDOMDocument.3.0");
        	xslDoc.async = false;
        	xslDoc.load(path + xslFile);
			if (xslDoc.parseError.errorCode != 0)
				alert(xslDoc.parseError);

			// transform the source using the XSLT stylesheet
        	div.innerHTML = xmlDoc.transformNode(xslDoc);

//			document.getElementById("myXML").insertAdjacentHTML('beforeEnd', 
//									xmlDoc.transformNode(xslDoc));

/*
			// XSL Transformation
			var xslTpl = new ActiveXObject("Msxml2.XSLTemplate.3.0");
			xslTpl.stylesheet = xslDoc;
			var xslProc = xslTpl.createProcessor();
			xslProc.input = xmlDoc;

			// Transform
			xslProc.transform;
       		myXML.innerHTML = xslProc.output;
*/
        	
      	}

      	function makeFireFoxTransformation(div, path, xmlFile, xslFile) {
			var XSLT = new XSLTProcessor;

			var xmlDoc = new XMLHttpRequest;
			xmlDoc.open('GET', path + xmlFile, false);
			xmlDoc.overrideMimeType('text/xml');
			xmlDoc.send(null);
			var xml = xmlDoc.responseXML;

			var xslDoc = new XMLHttpRequest;
			xslDoc.open('GET', path + xslFile, false);
			xslDoc.overrideMimeType('text/xml');
			xslDoc.send(null);
			var xsl = xslDoc.responseXML;

			XSLT.importStylesheet(xsl);

  			div.appendChild(XSLT.transformToFragment(xml, document));
//  			document.getElementById("myCSV").appendChild(XSLT.transformToFragment(xml, document));
		}      	
      	
	</SCRIPT>
