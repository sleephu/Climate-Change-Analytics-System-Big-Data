<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<head>
<title>GeoMap</title>
<script type="text/javascript" src="https://www.google.com/jsapi"></script>
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js"></script>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap-theme.min.css">
<%-- <link href="<c:url value="/resources/css/style.css" />" rel="stylesheet"> --%>
<script type="text/javascript">
//get data ajax
google.load('visualization', '1.0', {
'packages' : [ 'corechart' ]
});
google.setOnLoadCallback(drawChart);

function drawChart() {
var init = function() {
    $.getJSON('records?' + $('form').serialize(), function(data) {
	//data
	var actual=data[1];
	var predict = data[0];
	
	//temperature
	var table = new google.visualization.DataTable();
	table.addColumn('date', 'Time');
	table.addColumn('number', 'Avg Temperature (Actual)');
	table.addColumn('number', 'Avg Temperature (Prediction)');

	//diff
	var tempDiff = new google.visualization.DataTable();
	tempDiff.addColumn('date', 'Time');
	tempDiff.addColumn('number', 'Min diff');
	tempDiff.addColumn('number', '25 qt');
	tempDiff.addColumn('number', '75 qt');
	tempDiff.addColumn('number', 'Max diff');
	var monthDiff = [];
	//var monthMaxSum = 0;
	//var monthMin = [];
	//var monthMinSum = 0;
	//var days = 0;

	//prcp
	var prcp = new google.visualization.DataTable();
	prcp.addColumn('date', 'Month');
	prcp.addColumn('number', 'Precipitation');
	var monthPrcp = 0;
	
	//frshtt
	var frshtt = new google.visualization.DataTable();
	frshtt.addColumn('string', 'Phenomenom');
	frshtt.addColumn('number', 'X');
	frshtt.addColumn('number', 'Y');
	frshtt.addColumn('string', 'Occurence');
	frshtt.addColumn('number', 'Frequency');
	var monthFrshtt = {fog:0,rain:0,snow:0,hail:0,tunder:0,tornado:0};
	var frshttId = {fog:1,rain:2,snow:3,hail:4,tunder:5,tornado:6};
	
	//month
	var currMonth = actual.length>0?new Date(actual[0].time):new Date(predict[0].time);

	//loop
	var i=0;
	var trendLine = {};
	if(predict.length>=356*3) trendLine[1]={};
	if(actual.length>=356*3) trendLine[0]={};
	//console.log(data);
	for (; i < actual.length; i++) {
	    
	    var date = new Date(actual[i].time);
	    table.addRow([ date, actual[i].temp, null]);
	    //tempDiff.addRow([ date,
	    //                  actual[i].max , actual[i].min ]);
	    if (date.getMonth() !== currMonth.getMonth()) {
			//prcp aggregation
			prcp.addRow([ currMonth, monthPrcp ]);
			monthPrcp = 0;		
			//diff aggregation
			monthDiff.sort(function(a, b){return a-b});
			tempDiff.addRow([currMonth,monthDiff[0],monthDiff[Math.floor(monthDiff.length*0.25)]
							,monthDiff[Math.floor(monthDiff.length*0.75)],monthDiff[monthDiff.length-1]]);
			monthDiff = [];
			//frshtt aggregation			
			for(var x in monthFrshtt){
			    if (monthFrshtt[x]>0){
					frshtt.addRow([x,i-15,frshttId[x],x,monthFrshtt[x]]);
			    }			    
			}
			var monthFrshtt = {fog:0,rain:0,snow:0,hail:0,tunder:0,tornado:0};
			//set month
			currMonth = date;
	    }
	  	//prcp aggregation	  	
	    monthPrcp += actual[i].prcp;
	  	//diff aggregation	    
		monthDiff.push(actual[i].max-actual[i].min);
		//frshtt aggregation	 
		if(actual[i].frshtt!=null){
			for(var j=0;j<actual[i].frshtt.length;j++){
			    monthFrshtt[actual[i].frshtt[j]]+=1;
			}
		}
	}
	i=0;
	for (; i < predict.length; i++) {
	    var date = new Date(predict[i].time);
	    table.addRow([ date, i==0?predict[i].temp:null, predict[i].temp]);
	    //tempDiff.addRow([ date,
	    //                  predict[i].max , predict[i].min ]);  
	    if (date.getMonth() !== currMonth.getMonth()) {
			//diff aggregation
			monthDiff.sort(function(a, b){return a-b});
			
			tempDiff.addRow([currMonth,monthDiff[0],monthDiff[Math.floor(monthDiff.length*0.25)]
							,monthDiff[Math.floor(monthDiff.length*0.75)],monthDiff[monthDiff.length-1]]);
			monthDiff = [];
			//set month
			currMonth = date;
	    }
	  	//diff aggregation	  
	    monthDiff.push(predict[i].max-predict[i].min);
	}
	


	//wrapper
	//temperature
	var wrapperTemp = new google.visualization.ChartWrapper({
	    chartType : 'LineChart',
	    dataTable : table,
	    options : {
		'title' : 'Average Temperature (F)',
		'height' : 600,
		
		trendlines : trendLine
	    },
	    containerId : 'temperature'
	});
	//diff
	var wrapperDiff = new google.visualization.ChartWrapper({
	    chartType : 'CandlestickChart',
	    dataTable : tempDiff,
	    options : {
		'title' : 'Temperature Difference (F)',
		'height' : 400,
		colors:['#FF8533'],
		curveType: 'function',
		intervals: { 'style':'line' },
	    //backgroundColor: '#CCFF99'
	    legend:'bottom'
	    },
	    containerId : 'tempDiff'
	});
	//prcp
	var wrapperPrcp = new google.visualization.ChartWrapper({
	    chartType : 'BarChart',
	    dataTable : prcp,
	    options : {
		'title' : 'Precipitation (inch)',
		'height' : 400,
		colors:['#009999'],
	    //backgroundColor: '#FFDA91'
	    legend:'bottom'
	    },
	    containerId : 'prcp'
	});
	//frshtt
	var wrapperFrshtt = new google.visualization.ChartWrapper({
	    chartType : 'BubbleChart',
	    dataTable : frshtt,
	    options : {
		'title' : 'Occurence',
		'height' : 600,
		colors:['#009999'],
		series:{fog:{color:'grey'},rain:{color:'green'},snow:{color:"blue"},hail:{color:'purple'},tunder:{color:'orange'},tornado:{color:'red'}},
	    //backgroundColor: '#FFDA91'
	    vAxis:{minValue:0,maxValue:6}
	    },
	    containerId : 'frshtt'
	});

	//draw
	wrapperTemp.draw();
	wrapperDiff.draw();
	wrapperPrcp.draw();
	wrapperFrshtt.draw();
    });
}

init();
$('#ajax').on('click', init);
}


</script>
</head>
<body>
	<div class="jumbotron">
		<div class="container">
			<div class='row'>
				<h1>Analysis in Time Series</h1>
			</div>
			<div class='row'>
				<div class="col-md-12">
					<form action="records" method="post">
						Station ID: <input id='station' type='text' value='${station}' name='station' />
						Year: <input id='year' type='number' name='year' value="2011" min='1929' max='2016' /> <input id='ajax' type='button' value='Submit' />
						<a href='geomap'><input id='toMap' type='button' value='View Map' /></a>
					</form>
				</div>
			</div>
		</div>
	</div>


<div class="panel panel-default">
      <div class="panel-heading">
      	<h3 class="panel-title">Station Information</h3>
      </div>
      <div class="panel-body">
        <div class='row'>
			<div id='stationInfo' class="col-md-6">
			<p>Station name: ${sName}</p>
			<p>Country: ${sCountry}</p>
			</div>
			<div id='stationInfo' class="col-md-6">
			<p>Corrdinate: (${sLat} , ${sLon})</p>
			<p>Elevation: ${sElv} m</p>
			</div>
		</div>
      </div>
</div> 
 <div class="panel panel-default">
            <div class="panel-heading">
              <h3 class="panel-title">Average Temperature</h3>
            </div>
            <div class="panel-body">
           <div class='row'>
			<div id='temperature' class="col-md-12"></div>
		</div>
            </div>
          </div> 
          
          <div class="panel panel-default">
            <div class="panel-heading">
              <h3 class="panel-title">Temperature Difference & Precipitation</h3>
            </div>
          <div class="panel-body">
          <div class='row'>
			<div id='tempDiff' class="col-md-6"></div>
			<div id='prcp' class="col-md-6"></div>
		  </div>
            </div>
          </div>
          
           <div class="panel panel-default">
            <div class="panel-heading">
              <h3 class="panel-title">Occurence</h3>
            </div>
            <div class="panel-body">
           <div class='row'>
			<div id='frshtt' class="col-md-12"></div>
		</div>
            </div>
          </div>

          
                 
	<footer class="footer navbar-default">
      <div class="container">
        <p class="text-muted">Power by Cassandra & Spark.</p>
      </div>
    </footer>
    
    	
</body>

</html>