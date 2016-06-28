<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<head>
<title>Records</title>
<script type="text/javascript" src="https://www.google.com/jsapi"></script>
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js"></script>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap-theme.min.css">
<script type="text/javascript">
    //get data ajax
    google.load('visualization', '1.0', {
	'packages' : [ 'corechart' ]
    });
    google.setOnLoadCallback(drawChart);

    function drawChart() {
	//reder country
	function renderCountry() {
	    $.getJSON('countryCount', function(data) {
		//data
		//station
		var country = new google.visualization.DataTable();
		country.addColumn('string', 'country');
		country.addColumn('number', 'color');

		//loop
		var param;
		for (param in data) {
		    //console.log(data[param]);
		    country.addRow([ param, data[param] ]);

		}

		//wrapper
		//station
		var wrapperCountry = new google.visualization.ChartWrapper({
		    chartType : 'GeoChart',
		    dataTable : country,
		    options : {
			'title' : 'Countries',
			'displayMode' : 'region',
			'height' : 600
		    },
		    containerId : 'map'
		});

		//listener
		google.visualization.events.addListener(wrapperCountry,
			'select', onSelect);
		//on ready listener
		function onSelect() {
		    var selected = wrapperCountry.getChart().getSelection();
		    getSelectCountry(country.getValue(selected[0].row, 0));
		}

		//draw
		wrapperCountry.draw();
	    });
	}

	//get selected country listener
	function getSelectCountry(country) {
	    if (country == null) {
		country = $('#country_hidden').val();
	    } else {
		$('#country_hidden').val(country);
	    }
	    $
		    .getJSON(
			    'stationsByCountry?country=' + country + '&year='
				    + $('#year').val() + '&kmean='
				    + $('#kmean').val(),
			    function(data) {
				//data
				//station by country
				var station = new google.visualization.DataTable();
				station.addColumn('number', 'Latitude');
				station.addColumn('number', 'Longitude');
				station.addColumn('number', 'Category');
				//station stat
				var sstat = new google.visualization.DataTable();
				sstat.addColumn('string', 'Cluster');
				sstat.addColumn('number', 'Count');

				//loop
				//console.log(data);
				var clusterCount = {};
				for (var i = 0; i < data.length; i++) {
				    station.addRow([ data[i].latitude,
					    data[i].longitude,
					    Number(data[i].call) ]);
				    if (clusterCount[data[i].call] == null) {
					clusterCount[data[i].call] = 1;
				    } else {
					clusterCount[data[i].call] += 1;
				    }
				    ;
				}
				for ( var param in clusterCount) {
				    sstat
					    .addRow([ param,
						    clusterCount[param] ]);
				}
				//console.log(sstat);

				//wrapper
				//station by country
				var wrapperSBC = new google.visualization.ChartWrapper(
					{
					    chartType : 'GeoChart',
					    dataTable : station,
					    options : {
						'title' : 'Stations',
						'displayMode' : 'Markers',
						'region' : country,
						'height' : 600,
						sizeAxis : {
						    minValue : 0,
						    maxSize : 3
						},
						colorAxis : {
						    colors : ["#85C2FF","#00CC00","#FFFF00","#FF6600","#FF0000"]
						},
					    },
					    containerId : 'map'
					});
				//pir chart
				var wrapperStat = new google.visualization.ChartWrapper(
					{
					    chartType : 'PieChart',
					    dataTable : sstat,

					    options : {
						'title' : 'Clusters',
						height : 400,
						pieHole : 0.4,
						colors : ["#85C2FF","#00CC00","#FFFF00","#FF6600","#FF0000"]
					    },
					    
					    containerId : 'stationStat'
					});

				//listener
				google.visualization.events.addListener(
					wrapperSBC, 'select', onSelect);
				//on ready listener
				function onSelect() {
				    var selected = wrapperSBC.getChart()
					    .getSelection();
				    for (var i = 0; i < data.length; i++) {
					if (Math.abs(data[i].latitude- station.getValue(selected[0].row, 0)) < 0.01
						&& Math.abs(data[i].longitude - station.getValue(selected[0].row,1)) < 0.01) {
					    $('#stationPos:last').empty()
						    .append(
							    "<li>Station:<a href='/app/?station="
								    + data[i].station
								    +"'>"
								    + data[i].station
								    + "</a></li>")
						    .append(
							    "<li>Latitude:"
								    + data[i].latitude
								    + "</li>")
						    .append(
							    "<li>Longitude:"
								    + data[i].longitude
								    + "</li>")
						    .append(
							    "<li>Name:"
								    + data[i].stationName
								    + "</li>");
					    break;
					}
				    }

				}

				//draw
				wrapperSBC.draw();
				wrapperStat.draw();
			    });
	}

	//render country by default
	renderCountry();
	//invoke listener
	$('#worldmap').on('click', renderCountry);
	$('#cluster').on('click', function() {
	    getSelectCountry($('#country_hidden').val());
	});
	//$('#kmean').on('change', getSelectCountry($('#country_hidden').val()));

    }
</script>
</head>
<body>
	<div class="jumbotron">
		<div class="container">
			<div class='row'>
				<h1>Analysis in Geography</h1>
			</div>
			<div class='row'>
				<div class="col-md-12">
					<form id='form' action="records" method="post">
						Year: <input id='year' type="number" min='1929' max='2016' value='2013' /> Cluster (k): <input id='kmean' type="number" min='2' max='20' value='5' /> <input id='cluster' type='button'
							value='Go Cluster'
						/> <input id='worldmap' type='button' value='Show World Map' /> <input id='country_hidden' type="hidden" value="US" />
					</form>
				</div>
			</div>
		</div>
	</div>
	<div class="panel panel-default">
      <div class="panel-heading">
      	<h3 class="panel-title">Map</h3>
      </div>
      <div class="panel-body">
	
		<div class='row'>
			<div id='map' class="col-md-12"></div>
		</div>
		<div class="panel panel-default">
	      <div class="panel-heading">
	      	<h3 class="panel-title">Info</h3>
	      </div>
	      <div class="panel-body">
		<div class='row'>
			<div class="col-md-6">
			<h4>
				<ul id="stationPos">
				</ul>
			</h4>
			</div>
			<div id='stationStat' class="col-md-6"></div>
		</div>
		</div>
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

