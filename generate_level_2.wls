#!/usr/bin/env wolframscript


dataFolder = "data";
AllCells = True;
epsilon = 0.0000001;
level1DataFileName = FileNameJoin[{dataFolder, "nested_zome_ball_1.wl"}];
level2DataFileName = FileNameJoin[{dataFolder, "nested_zome_ball_2.wl"}];
phi = (1 + Sqrt[5])/2;
useAllVertices = False;

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

inputData = Get[level1DataFileName];
vertices1 = inputData["vertices"];
edges1 = inputData["edges"];
rectFaces1 = inputData["rectFaces"];
rectFacesWithCenter1 = Map[{{Simplify[Mean[#]]}, #}&, rectFaces1];

vertices = {};
edges = {};
rectFacesWithCenter = {};

selectedVertices1 = If[useAllVertices,
    vertices1,
    seed = vertices1[[1]];
    neighborEdges = Select[edges1, Length[Intersection[#, {seed} ]] > 0&];
    Union[Flatten[neighborEdges, 1]]
];

selectedEdges = If[useAllVertices,
    edges1,
    neighborEdges
];

scalingFactor = 2 * phi^3;

For[i = 1, i <= Length[selectedVertices1], i++,
    offset = selectedVertices1[[i]] * scalingFactor;
    newVertices = Map[# + offset &, vertices1]//Simplify;
    newEdges = Map[# + offset &, edges1, {2}]//Simplify;
    newRectFaceWithCenter = Map[# + offset &, rectFacesWithCenter1, {3}]//Simplify;
    vertices = Join[vertices, newVertices];
    edges = Join[edges, newEdges];
    rectFacesWithCenter = Join[rectFacesWithCenter, newRectFaceWithCenter];
    Print[{i, Length[vertices], Length[edges], Length[rectFacesWithCenter]}];
];

Print["vertices count: " <> IntegerString[Length[vertices]]];
Print["edge count: " <> IntegerString[Length[edges]]];

radius = Max[Flatten[vertices1]];
rectFacesMap = Association[Map[#[[1,1]] -> #[[2]]&, rectFacesWithCenter ]];

For[i = 1, i <= Length[selectedEdges], i++,
    edge = selectedEdges[[i]];
    edgeCenter = Mean[edge];
    newEdgeCore = edge * scalingFactor//Simplify;
    newEdgeCenter = Mean[newEdgeCore];
    newCenteredEdge = Map[# - newEdgeCenter&, newEdgeCore];
    shortCenteredEdge = newCenteredEdge / edgeLength[newEdgeCore] * (edgeLength[newEdgeCore] - 2 radius);
    newEdgePrototype = Map[# + newEdgeCenter&, shortCenteredEdge]//FullSimplify;
    realStartPoints = rectFacesMap[newEdgePrototype[[1]]];
    offsets = Map[# - newEdgePrototype[[1]]&, realStartPoints];
    newEdges = Table[
        Map[# + offset&, newEdgePrototype],
        {offset, offsets}
    ];
    edges = Join[edges, newEdges];
    Print[{i, Length[edges]}];
];

verticesStep2 = Union[Flatten[edges, 1], SameTest -> sameVertex];

Print["vertices count: " <> IntegerString[Length[verticesStep2]]];
Print["edge count: " <> IntegerString[Length[edges]]];

If[Length[verticesStep2] != Length[vertices], Throw["After step 2, there are new vertices"]];

data = <|
    "vertices" -> vertices,
    "edges" -> edges
|>;

If[FileExistsQ[level2DataFileName], DeleteFile[level2DataFileName]];
Save[level2DataFileName, data];
Print["Saved cells to " <> level2DataFileName];
