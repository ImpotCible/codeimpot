// Directive qui permet d'afficher un diagramme de type tilfordtree à partir du contenu de la variable $scope.proches
app.directive('tilfordtree', function() {

	return {
		restrict : 'A',
		link : function(scope, element, attr, ctrl) {
			
			var diameter = d3.select(element[0]).node().getBoundingClientRect().width - 30;
			var comparables = [];
			var form;
			var svg = null;

			// On compare chaque attribut en oubliant les attributs à exclure
			var getChildren = function(pProches, pExclusions) {
				var aRetourner = [];
		        var exclusions = [];
		        var childs = [];
		        var attribut;

		        if (pExclusions !== undefined) {
		        	exclusions = pExclusions.slice(); // Copy pour éviter la modification par d'autres appels à la méthode getChild
		        }

				for (var i in comparables) {
					childs = [];
					attribut = comparables[i];

					if(exclusions.indexOf(attribut) >= 0) {
						continue;
					} // else

					for(var j in pProches) {
						if(form[attribut] === undefined || pProches[j][attribut] === undefined) {
		                    continue;
						} // else
		                
		                if(form[attribut] != pProches[j][attribut]) {
		                	childs.push(pProches[j]);
		                }
					}

					if (childs.length > 0) {
		            	exclusions.push(attribut);
		                aRetourner.push({
		                	name: childs.length + " " + attribut, 
		                	children: getChildren(childs, exclusions),
		                	size: childs.length
		                });
		            	exclusions.splice(exclusions.indexOf(attribut));
					}
				}

				return aRetourner;
			};

			function setSize() {
				diameter = d3.select(element[0]).node().getBoundingClientRect().width - 30; // 15px margin left & right
			}
			
			var setForm = function(pForm) {
				form = pForm;
			}
			
			var setComparables = function(pComparables) {
				if(pComparables === undefined) {
					pComparables = [];
					for(var attr in form) {
						pComparables.push(attr);
					}
				}
				comparables = pComparables;
			}
			
			var draw = function(proches) {
				data = {
					'name' : 'YOU',
					'children' : getChildren(proches, ['id', 'cluster', 'distance', 'nombreEnfants', 'codesRev'])
				};
	
				if(svg == null) {
					svg = d3.select(element[0]).append("svg").attr("width", diameter).attr("height", diameter).attr("id", "tilfordtree")
					                           .append("g").attr("transform", "translate(" + diameter / 2 + "," + diameter / 2 + ")");
				} else {
					svg.empty();
				}
				
				var tree = d3.layout.tree()
					.size([360, diameter / 2 - 120])
					.separation(function(a, b) { return (a.parent == b.parent ? 1 : 2) / a.depth; });
	
				var diagonal = d3.svg.diagonal.radial()
					.projection(function(d) { return [d.y, d.x / 180 * Math.PI]; });
				
				var nodes = tree.nodes(data),
					links = tree.links(nodes);

				var link = svg.selectAll(".link")
					.data(links)
					.enter().append("path")
					.attr("class", "link")
					.attr("d", diagonal);

				var node = svg.selectAll(".node")
					.data(nodes)
					.enter().append("g")
					.attr("class", "node")
					.attr("transform", function(d) { return "rotate(" + (d.x - 90) + ")translate(" + d.y + ")"; })

				node.append("circle")
					.attr("r", 4.5);

				node.append("text")
					.attr("dy", ".31em")
					.attr("text-anchor", function(d) { return d.x < 180 ? "start" : "end"; })
					.attr("transform", function(d) { return d.x < 180 ? "translate(8)" : "rotate(180)translate(-8)"; })
					.text(function(d) { return d.name; });

				d3.select(self.frameElement).style("height", diameter - 150 + "px");
			}

			// Le $watch a pour but de mettre à jour le graphe dès que les
			// données présentent dans $scope.proches changent.
			// Ex : suppression ou ajout de noeuds
			scope.$watch("proches", function(proches) {
				console.log(proches);
				setForm(scope.declarant);
				setComparables();
				
				if (proches != null) {
					draw(proches);
				}
			});
		}
	}
});