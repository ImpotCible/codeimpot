// Directive qui permet d'afficher un diagramme de type ForceLayout à partir du contenu de la variable $scope.proches
app.directive('forceLayout', function() {

	return {
		restrict : 'A',
		link : function(scope, element, attr, ctrl) {

			var height = null;
			var width  = null;
			var svg;
			
			// Dégradé de couleurs pour mettre en évidence la différence d'IR, du vert au rouge
			var color = d3.scale.threshold().domain([-25, -15, -5, 5, 15, 25]).range(["#08C91F", "#ABEDB2", "#E1FAE4", "#FFFFFF", "#F0E9B9", "#EDB90C", "#bb0043"]);
			
			// Map qui définit la couleur du texte dans le cercle en fonction de sa couleur de fond
			var mapTextColor = {};
			mapTextColor["#bb0043"] = "white";
			mapTextColor["#EDB90C"] = "white";
			mapTextColor["#F0E9B9"] = "black";
			mapTextColor["#FFFFFF"] = "black";
			mapTextColor["#E1FAE4"] = "black";
			mapTextColor["#ABEDB2"] = "black";
			mapTextColor["#08C91F"] = "white";

			// Construire le dataset à afficher à partir du contenu de $scope.proches
			function buildDataset(proches) {			
				var dataset = {};
				dataset["nodes"] = new Array();
				var moi = {					
					"montantIR" : scope.declarant.montantIR,
					"rayon" : 50,
					"color" : "#0275d8",
					"root" : true,
				};
				dataset["nodes"].push(moi);
				var i = 1;
				// Créer les noeuds du graphe
				for ( var declarant in proches) {
					proches[declarant]["rayon"] = 30;
					proches[declarant]["ratio"] = parseInt(100 * (proches[declarant].montantIR - moi.montantIR) / moi.montantIR, 10);
					proches[declarant]["color"] = color(proches[declarant]["ratio"]);
					proches[declarant]["textColor"] = mapTextColor[proches[declarant]["color"]];
					dataset["nodes"].push(proches[declarant]);
					i++;
				}
				dataset["links"] = new Array();
				// Définir les liens entre le centre les autres noeuds 
				i = 0;
				for ( var declarant in proches) {
					var link = {
						"source" : 0,
						"target" : i + 1,
						"distance" : proches[i].distance
					};
					dataset["links"].push(link);
					i++;
				}
				return dataset;
			}
			
			function setSize() {
				width = d3.select(element[0]).node().getBoundingClientRect().width - 30; // 15px margin left & right
				height = d3.select('#graph-forcelayout').node().getBoundingClientRect().height; // 50 margin top & bottom
			}

			// Affiche le dataset
			function draw(graph) {
				
				// Construire le conteneur SVG
				var margin = {left : 20, top : 20};
				
				if(svg != null) {
					svg.remove();
				}
				
				svg = d3.select(element[0]).append("svg").attr("width", width).attr("height", height).attr("id", "forceLayout");

				// Remplir le SVG avec le dataset
				var force = d3.layout.force().nodes(graph.nodes)
					.linkDistance(function(d) {
						return d.distance;
					})
					.links(graph.links)
					.gravity(0.5)
					.charge(-5000)
					.size([width, height])
					.start();

				// Style sur les liens
				var link = svg.selectAll(".link")
				.data(graph.links)
				.enter().append("line").attr("class", "link").style("stroke-width", 6);

				var transitionDuration = 200;
				var initialFontSize = "14px";
				
				var node = svg.selectAll(".node").data(graph.nodes)
				.enter().append("g")
				.attr("class", "node")
				.call(force.drag)
				.on("mouseover", function(d, i) { // Au survol d'un noeud, animation
					if(!d.root) {
						d3.select(this).select("circle").transition()        
		                .duration(transitionDuration)
		                .attr("r", 50)
		                .attr("stroke", "black")
		                .attr("stroke-width", 5);
					}
					d3.select(this).select("text").transition()        
	                .duration(transitionDuration)	                
	                .attr("font-size", "22px");

					// Définir le noeud selectionné dans le scope AngularJS
					scope.$apply(function(){
					   scope.proche = d;
	
					});					
				})
				.on("mouseout", function(d, i) { // En quittant le noeud, animation inverse
					if(!d.root) {
						d3.select(this).select("circle").transition()        
		                .duration(transitionDuration)      
		                .attr("r", 30)		                
		                .attr("stroke", "grey")
		                .attr("stroke-width", 1);
						d3.select(this).select("text").transition()        
		                .duration(transitionDuration)		                
		                .attr("font-size", initialFontSize);
					}
					// Supprimer le noeud courant du scope
					scope.$apply(function(){
					   scope.proche = null;

					});	
				}).on("click", function(d,i) {
					if(!d.root) {
						scope.$apply(function() {
							scope.setIndividuCompare(d);
						});
					}
				});

				// Dessiner les cercles
				node.append("circle")
				.attr("r", function(d) {
					return d.rayon;
				}).style("fill", function(d) {
					return d.color;
				}).attr("stroke-width", 1)
				.attr("stroke", "grey")
				.attr("cursor", "pointer");
				
				// Ajouter un contenu textuel dans les cercles
				node.append("text").attr("dy", "0.4em").style("text-anchor", "middle").attr("font-size", initialFontSize).attr("cursor", "pointer")
				.attr("fill", function(d) {return d.textColor})
				.text(function(d) {
					if(!d.root) {
						return d.ratio > 0 ? ("+" + d.ratio + "%") : (d.ratio + "%");
					}
				});

				// Style le noeud racine
				var root = svg.select(".node");				
				root.append("text").attr("dy", ".2em").style("text-anchor",	"middle").attr("font-size", "18px").attr("fill", "white").attr("cursor", "pointer")
				.text("Vous êtes");				
				root.append("text").attr("dy", "1.2em").style("text-anchor",	"middle").attr("font-size", "18px").attr("fill", "white").attr("cursor", "pointer")
				.text("ici");								
				
				// Animation sur le noeud racine
				root.transition()
			    .duration(1500)			    
			    .each(loopCircle);

				function loopCircle() {
				  var circle = d3.select(this).select("circle");
				  (function repeat() {
				    circle = circle.transition()
				        .attr("r", 55)
				        .attr("stroke-width", 5)
				      .transition()
				        .attr("r", 50)
				        .attr("stroke-width", 0)
				        .each("end", repeat);
				  })();
				}

				// Configuration force layout
				force.on("tick", function() {
					link.attr("x1", function(d) {
						return d.source.x;
					}).attr("y1", function(d) {
						return d.source.y;
					}).attr("x2", function(d) {
						return d.target.x;
					}).attr("y2", function(d) {
						return d.target.y;
					});

					node.attr("transform", function(d) {
						return "translate(" + d.x + "," + d.y + ")";
					});
				});
			}
			
			setSize();

			// Le $watch a pour but de mettre à jour le graphe dès que les
			// données présentent dans $scope.proches changent.
			// Ex : suppression ou ajout de noeuds
			scope.$watch("proches", function(proches) {
				if (proches != null) {
					draw(buildDataset(proches));
				}
			});
			
			d3.select(window).on('resize', function () {
				svg.attr("width", 0)
				setSize();
				svg.attr("width", width).attr("height", height);
	        });
		}
	}
});
