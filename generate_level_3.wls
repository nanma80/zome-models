#!/usr/bin/env wolframscript


dataFolder = "data";
AllCells = True;
epsilon = 0.0000001;
level1DataFileName = FileNameJoin[{dataFolder, "nested_zome_ball_1.wl"}];
level2DataFileName = FileNameJoin[{dataFolder, "nested_zome_ball_2.wl"}];
level3DataFileName = FileNameJoin[{dataFolder, "nested_zome_ball_3.wl"}];

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

level1 = Get[level1DataFileName];
vertices1 = level1["vertices"];
edges1 = level1["edges"];
rectFaces1 = level1["rectFaces"];

level2 = Get[level2DataFileName];
vertices2 = level2["vertices"];
edges2 = level2["edges"];

radius = Max[Flatten[vertices2]];
(* Print[radius]; *)

f[d_] := (phi*d - 2 radius)/(d - 2 radius);

sol = Solve[f[d] == phi^2, {d}][[1]];
shortStrutLength = (d - 2 radius)/.sol//Simplify;
longStrutLength = (phi*d - 2 radius)/.sol//Simplify;

shortImplement = 2 * phi^7 + 2 * phi//Simplify;
longImplement = 2 * phi^9 + 2 * phi^3//Simplify;

Print[shortStrutLength == shortImplement];
Print[longStrutLength == longImplement];

scalingFactor = d/2/.sol;
Print[scalingFactor];
Print[scalingFactor//N];

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

For[i = 1, i <= Length[selectedVertices1], i++,
    offset = selectedVertices1[[i]] * scalingFactor;
    newVertices = Map[# + offset &, vertices2]//Simplify;
    newEdges = Map[# + offset &, edges2, {2}]//Simplify;
    (* newRectFaceWithCenter = Map[# + offset &, rectFacesWithCenter1, {3}]//Simplify; *)
    vertices = Join[vertices, newVertices];
    edges = Join[edges, newEdges];
    (* rectFacesWithCenter = Join[rectFacesWithCenter, newRectFaceWithCenter]; *)
    (* Print[{i, Length[vertices], Length[edges], Length[rectFacesWithCenter]}]; *)
    Print[{i, Length[vertices], Length[edges]}];
];

Print["vertices count: " <> IntegerString[Length[vertices]]];
Print["edge count: " <> IntegerString[Length[edges]]];

data = <|
    "vertices" -> vertices,
    "edges" -> edges
|>;

If[FileExistsQ[level3DataFileName], DeleteFile[level3DataFileName]];
Save[level3DataFileName, data];
Print["Saved data to " <> level3DataFileName];
