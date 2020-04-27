#!/usr/bin/env wolframscript

dataFolder = "data";
AllCells = True;
epsilon = 0.0000001;
level1DataFileName = FileNameJoin[{dataFolder, "golden_field_nested_zome_ball_1.wl"}];
level2DataFileName = FileNameJoin[{dataFolder, "golden_field_nested_zome_ball_2.wl"}];
level3DataFileName = FileNameJoin[{dataFolder, "golden_field_nested_zome_ball_3.wl"}];
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
goldenMultiply[x_, y_] := {
    x[[1]] * y[[1]] + x[[2]] * y[[2]],
    x[[1]] * y[[2]] + x[[2]] * y[[1]] + x[[2]] * y[[2]]
};
goldenScale[vector_, scale_] := Map[goldenMultiply[scale, #]&, vector];
goldenDot[vector1_, vector2_] := Total[Table[goldenMultiply[vector1[[k]], vector2[[k]]], {k, Length[vector1]}]];
toReal[goldenVector_] := goldenVector[[1]] + phi * goldenVector[[2]]//Simplify;
getMap[list_] := Association[Table[{list[[k]] -> k}, {k, 1, Length[list]}]];
edgeLengthSquare[edge_] := goldenDot[edge[[1]] - edge[[2]], edge[[1]] - edge[[2]]].{1, phi}//Simplify;
goldenNorm[v_] := Sqrt[goldenDot[v, v].{1, phi}];

inputData1 = Get[level1DataFileName];
vertices1 = inputData1["vertices"];
edges1 = inputData1["edges"];
rectFaceCenters = inputData1["rectFaceCenters"];

inputData2 = Get[level2DataFileName];
vertices2 = inputData2["vertices"];
edges2 = inputData2["edges"];
maxDot = SortBy[ Map[goldenDot[rectFaceCenters[[1]], #]&, vertices2 ], -N[toReal[#]]&][[1]];

rectFace[center_] := Select[vertices2, goldenDot[center, #] == maxDot&];
rectFacesWithCenter2 = Map[{{Mean[rectFace[#]]}, rectFace[#] }&, rectFaceCenters ];

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

(* 2 (20 + 9 Sqrt[5]) *)
scalingFactor = {22, 36};

flatEdges1 = Map[vertices1[[#]]&, edges1];
flatEdges2 = Map[vertices2[[#]]&, edges2];

For[i = 1, i <= Length[selectedVertices1], i++,
    offset = goldenScale[vertices1[[selectedVertices1[[i]] ]], scalingFactor];
    newVertices = Map[# + offset &, vertices2];
    newEdges = Map[# + offset &, flatEdges2, {2}];
    newRectFaceWithCenter = Map[# + offset &, rectFacesWithCenter2, {3}]//Simplify;
    vertices = Join[vertices, newVertices];
    edges = Join[edges, newEdges];
    centerEdgeMap[offset] = newRectFaceWithCenter;
    Print[{i, Length[vertices], Length[edges]}];
];

Print["Step 2: add composite edges"];

For[i = 1, i <= Length[selectedEdges], i++,
    edge = vertices1[[ selectedEdges[[i]] ]];
    edgeCenter = Mean[edge];
    newEdgeCore = Map[goldenScale[#, scalingFactor]&, edge];
    newEdgeCenter = Mean[newEdgeCore];

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

    (* newEdges = Flatten[ Map[{{#[[1]], Mean[#]}, {#[[2]], Mean[#]}}&, newDoubleEdges], 1]; *)
    (* edges = Join[edges, newEdges]; *)
    (* This is not right. the edges are not proper *)
    (* not using them first *)
    (* edges = Join[edges, newDoubleEdges]; *)

    Print[{i, Length[edges]}];
 ];

Print["edge count: " <> IntegerString[Length[edges]]];

vertices = Union[Flatten[edges, 1]];
verticesMap = getMap[vertices];

edgeIndices = Map[Sort[Map[verticesMap, #]]&, edges];

data = <|
    "vertices" -> vertices,
    "edges" -> edgeIndices
|>;

Print["edge lengths:"];
lengths = FullSimplify[Sqrt[Union[Map[edgeLengthSquare, edges]]]];
Print["levels"];
Print[Log[phi, lengths/2]//N];

Print["Final vertex count and edge count:"];
Print[Length[vertices]];
Print[Length[edgeIndices]];

If[FileExistsQ[level3DataFileName], DeleteFile[level3DataFileName]];
Save[level3DataFileName, data];
Print["Saved data to " <> level3DataFileName];
