#!/usr/bin/env wolframscript

dataFolder = "data";
AllCells = True;
epsilon = 0.0000001;
level1DataFileName = FileNameJoin[{dataFolder, "golden_field_nested_zome_ball_1.wl"}];
level2DataFileName = FileNameJoin[{dataFolder, "golden_field_nested_zome_ball_2.wl"}];
phi = (1 + Sqrt[5])/2;
useAllVertices = True;

projectTo3d[vertex4d_] := {vertex4d[[1]], vertex4d[[2]], vertex4d[[3]]};
reflect[point_, mirror_] := Simplify[point - 2 * (point.mirror)/(mirror.mirror)*mirror];
doubleReflect[point_, mirror1_, mirror2_] := reflect[reflect[point, mirror1], mirror2];
getEdgesFromFace[face_] := Table[{face[[i+1]], face[[Mod[i+1, Length[face]] + 1]]}, {i, 0, Length[face] - 1}];
explodedFace[face_, explosionFactor_] := Map[(# + Mean[face] * explosionFactor)&, face];
sameVertex[point1_, point2_] := point1 == point2;
sameNVertex[point1_, point2_] := Norm[N[point1] - N[point2]] < epsilon;
sameCenter[edge1_, edge2_] := Total[edge1] == Total[edge2];
sameCellCenter[cell1_, cell2_] := getCellCenter[cell1] == getCellCenter[cell2];
sameNCellCenter[cell1_, cell2_] := Norm[getCellCenter[N[cell1]] - getCellCenter[N[cell2]]] < epsilon;
getCellCenter[cell_] := Mean[Flatten[cell, 1]];
edgeLength[edge_] := Norm[edge[[1]] - edge[[2]]];
goldenMultiply[x_, y_] := {
    x[[1]] * y[[1]] + x[[2]] * y[[2]],
    x[[1]] * y[[2]] + x[[2]] * y[[1]] + x[[2]] * y[[2]]
};
goldenScale[vector_, scale_] := Map[goldenMultiply[scale, #]&, vector];
goldenDot[vector1_, vector2_] := Total[Table[goldenMultiply[vector1[[k]], vector2[[k]]], {k, Length[vector1]}]];
toReal[goldenVector_] := goldenVector[[1]] + phi * goldenVector[[2]]//Simplify;
getMap[list_] := Association[Table[{list[[k]] -> k}, {k, 1, Length[list]}]];

inputData = Get[level1DataFileName];
vertices1 = inputData["vertices"];
edges1 = inputData["edges"];
rectFaceCenters = inputData["rectFaceCenters"];
maxDot = SortBy[ Map[goldenDot[rectFaceCenters[[1]], #]&, vertices1 ], -N[toReal[#]]&][[1]];

rectFace[center_] := Select[vertices1, goldenDot[center, #] == maxDot&];
rectFacesWithCenter1 = Map[{{#}, rectFace[#] }&, rectFaceCenters ];

vertices = {};
edges = {};
rectFacesWithCenter = {};
centerEdgeMap = <||>;

selectedVertices1 = If[useAllVertices,
    Range[Length[vertices1]],
    seed = 1;
    neighborEdges = Select[edges1, Length[Intersection[#, {seed} ]] > 0&];
    Union[Flatten[neighborEdges, 1]]
];

selectedEdges = If[useAllVertices,
    edges1,
    neighborEdges
];

scalingFactor = goldenMultiply[{2, 0}, 
                goldenMultiply[{0, 1}, 
                goldenMultiply[{0, 1}, 
                    {0, 1}]]];

flatEdges1 = Map[vertices1[[#]]&, edges1];

For[i = 1, i <= Length[selectedVertices1], i++,
    offset = goldenScale[vertices1[[selectedVertices1[[i]] ]], scalingFactor];
    newVertices = Map[# + offset &, vertices1];
    newEdges = Map[# + offset &, flatEdges1, {2}];
    newRectFaceWithCenter = Map[# + offset &, rectFacesWithCenter1, {3}]//Simplify;
    vertices = Join[vertices, newVertices];
    edges = Join[edges, newEdges];
    centerEdgeMap[offset] = newRectFaceWithCenter;
    (* rectFacesWithCenter = Join[rectFacesWithCenter, newRectFaceWithCenter]; *)
    Print[{i, Length[vertices], Length[edges]}];
];

(* Print["vertices count: " <> IntegerString[Length[vertices]]];
Print["edge count: " <> IntegerString[Length[edges]]];

vertices = Union[Flatten[edges, 1]];
verticesMap = getMap[vertices];

edgeIndices = Map[Sort[Map[verticesMap, #]]&, edges];

(* data = <|
    "vertices" -> vertices,
    "edges" -> edgeIndices
|>;
 *)
Print[Length[vertices]];
Print[Length[edgeIndices]];
 *)

(* If[FileExistsQ[level2DataFileName], DeleteFile[level2DataFileName]];
Save[level2DataFileName, data];
Print["Saved data to " <> level2DataFileName];
 *)

(* radius = SortBy[ Flatten[vertices1, 1], -N[toReal[#]]&][[1]]; *)
(* rectFacesMap = Association[Map[#[[1,1]] -> #[[2]]&, rectFacesWithCenter ]]; *)
Print["Step 2: add composite edges"];

For[i = 1, i <= Length[selectedEdges], i++,
    edge = vertices1[[ selectedEdges[[i]] ]];
    edgeCenter = Mean[edge];
    newEdgeCore = Map[goldenScale[#, scalingFactor]&, edge];
    newEdgeCenter = Mean[newEdgeCore];
    (* newCenteredEdge = Map[# - newEdgeCenter&, newEdgeCore]; *)

    rectFaceWithCenter1 = SortBy[centerEdgeMap[newEdgeCore[[1]]],
        -goldenDot[#[[1,1]]-newEdgeCore[[1]], newEdgeCenter - newEdgeCore[[1]]].{{1.}, {phi}}&
    ][[1]];

    rectFaceWithCenter2 = SortBy[centerEdgeMap[newEdgeCore[[2]]],
        -goldenDot[#[[1,1]]-newEdgeCore[[2]], newEdgeCenter - newEdgeCore[[2]]].{{1.}, {phi}}&
    ][[1]];

    realStartPoint1 = rectFaceWithCenter1[[2]];
    offsets = Map[# - rectFaceWithCenter1[[1, 1]]&, realStartPoint1];
    newEdgePrototype = {rectFaceWithCenter1[[1, 1]], rectFaceWithCenter2[[1, 1]]};

    newDoubleEdges = Table[
        Map[# + offset&, newEdgePrototype],
        {offset, offsets}
    ];
    newEdges = Flatten[ Map[{{#[[1]], Mean[#]}, {#[[2]], Mean[#]}}&, newDoubleEdges], 1];
    edges = Join[edges, newEdges];
    Print[{i, Length[edges]}];


(*     (* how can I do this? *)
    shortCenteredEdge = newCenteredEdge / edgeLength[newEdgeCore] * (edgeLength[newEdgeCore] - 2 radius);

    newEdgePrototype = Map[# + newEdgeCenter&, shortCenteredEdge]//FullSimplify;
    realStartPoints = rectFacesMap[newEdgePrototype[[1]]];
    offsets = Map[# - newEdgePrototype[[1]]&, realStartPoints];

    newDoubleEdges = Table[
        Map[# + offset&, newEdgePrototype],
        {offset, offsets}
    ];
    newEdges = Flatten[ Map[{{#[[1]], Mean[#]}, {#[[2]], Mean[#]}}&, newDoubleEdges], 1];
    edges = Join[edges, newEdges];
    Print[{i, Length[edges]}];
 *)
 ];

Print["vertices count: " <> IntegerString[Length[vertices]]];
Print["edge count: " <> IntegerString[Length[edges]]];

vertices = Union[Flatten[edges, 1]];
verticesMap = getMap[vertices];

edgeIndices = Map[Sort[Map[verticesMap, #]]&, edges];

data = <|
    "vertices" -> vertices,
    "edges" -> edgeIndices
|>;

Print[Length[vertices]];
Print[Length[edgeIndices]];


(* verticesStep2 = Union[Flatten[edges, 1], SameTest -> sameVertex];

Print["vertices count: " <> IntegerString[Length[verticesStep2]]];
Print["edge count: " <> IntegerString[Length[edges]]];

data = <|
    "vertices" -> verticesStep2,
    "edges" -> edges
|>;
 *)

If[FileExistsQ[level2DataFileName], DeleteFile[level2DataFileName]];
Save[level2DataFileName, data];
Print["Saved data to " <> level2DataFileName];
