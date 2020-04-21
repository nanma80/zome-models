#!/usr/bin/env wolframscript


dataFolder = "data";
AllCells = True;
epsilon = 0.0000001;
level1DataFileName = FileNameJoin[{dataFolder, "nested_zome_ball_1.wl"}];
level2DataFileName = FileNameJoin[{dataFolder, "nested_zome_ball_2.wl"}];
phi = (1 + Sqrt[5])/2;

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

inputData = Get[level1DataFileName];
vertices1 = inputData["vertices"];
edges1 = inputData["edges"];

Print[edges1//Length];
Print[vertices1//Length];

vertices = {};
edges = {};

limit = Length[vertices1];
(* limit = 9; *)
seed = vertices1[[1]];
neighborEdges = Select[edges1, Length[Intersection[#, {seed} ]] > 0&];
selectedVertices1 = Union[Flatten[neighborEdges, 1]];
limit = Length[selectedVertices1];

For[i = 1, i <= limit, i++,
    offset = selectedVertices1[[i]] * 2 * phi^3;
    newVertices = Map[# + offset &, vertices1]//Simplify;
    newEdges = Map[# + offset &, edges1, {2}]//Simplify;
    vertices = Join[vertices, newVertices];
    edges = Join[edges, newEdges];
    Print[{i, Length[vertices], Length[edges]}];
];

Print[edges//Length];
Print[vertices//Length];

data = <|
    "vertices" -> vertices,
    "edges" -> edges
|>;

If[FileExistsQ[level2DataFileName], DeleteFile[level2DataFileName]];
Save[level2DataFileName, data];
Print["Saved cells to " <> level2DataFileName];
