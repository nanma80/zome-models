#!/usr/bin/env wolframscript

dataFolder = "data";
epsilon = 0.0000001;

level = 1;
numberLimit = {20, 20};

shape = "nested_zome_ball_" <> IntegerString[level];
dataFileName = FileNameJoin[{dataFolder, shape <> ".wl"}];

projectTo3d[vertex4d_] := {vertex4d[[1]], vertex4d[[2]], vertex4d[[3]]};
reflect[point_, mirror_] := FullSimplify[point - 2 * (point.mirror)/(mirror.mirror)*mirror];
doubleReflect[point_, mirror1_, mirror2_] := reflect[reflect[point, mirror1], mirror2];
getEdgesFromFace[face_] := Table[{face[[i+1]], face[[Mod[i+1, Length[face]] + 1]]}, {i, 0, Length[face] - 1}];
explodedFace[face_, explosionFactor_] := Map[(# + Mean[face] * explosionFactor)&, face];
sameVertex[point1_, point2_] := point1 == point2;
sameCenter[edge1_, edge2_] := Total[edge1] == Total[edge2];
sameCellCenter[cell1_, cell2_] := getCellCenter[cell1] == getCellCenter[cell2];
getCenter[edge_] := Total[edge];
getCellCenter[cell_] := Simplify[Total[Flatten[cell, 1]]];
edgeLength[edge_] := Norm[edge[[1]] - edge[[2]]];

toGoldenField[value_, lookupTable_] := 
	If[value < -epsilon,
		- toGoldenField[-value, lookupTable],
		Intersection[lookupTable, {{N[value]}}, SameTest -> (Abs[(#1[[1]] - #2[[1]])] < epsilon&)][[1,2]]
	];

getMap[list_] := Association[Table[{list[[k]] -> k}, {k, 1, Length[list]}]];

getEdgeVertexData[edges_] := Module[
	{
		possibleValues, filteredTable, edgesGoldenField, vertices, verticesMap, edgeIndices
	},
	
	Print["Converting " <> IntegerString[Length[edges]] <> " edges to index based."];
	possibleValues = Union[Flatten[edges], SameTest -> (Abs[N[#1]-N[#2]]<epsilon&)];
	Print["possibleValues count: " <> IntegerString[Length[possibleValues]]];
	missingValues = Complement[Table[{N[Abs[v]]}, {v, possibleValues}], lookupTable, SameTest -> (Abs[(#1[[1]] - #2[[1]])] < epsilon&)];
	Print["missingValues checked"];

	If[Length[missingValues] > 0, 
		Print["There are missing values " <> ToString[Flatten[missingValues]]]
	];

	filteredTable = Intersection[lookupTable, Table[{N[Abs[v]]}, {v, possibleValues}], SameTest -> (Abs[(#1[[1]] - #2[[1]])] < epsilon&)];
	Print["filteredTable built"];

	edgesGoldenField = Map[toGoldenField[#, filteredTable]&, edges, {3}];
	Print["Mapped values to the golden ratio field"];

	vertices = Union[Flatten[edgesGoldenField, 1]];
	verticesMap = getMap[vertices];

	edgeIndices = Map[Sort[Map[verticesMap, #]]&, edgesGoldenField];

	<|
		"vertices" -> vertices,
		"edges" -> edgeIndices
	|>
];

phi = (1 + Sqrt[5])/2;
lookupTable = Flatten[Table[
	{N[i + j * GoldenRatio], {i, j}},
	{i, 0, numberLimit[[1]]},
	{j, 0, numberLimit[[2]]}
], 1];

outputFileName = "golden_field_" <> shape <> ".wl";
dataFileName = FileNameJoin[{dataFolder, shape <> ".wl"}];
outputFileName = FileNameJoin[{dataFolder, outputFileName}];

Print["Reading data for " <> shape <> " from " <> dataFileName];

inputData = Get[dataFileName];

scalingFactor = 1;
data = getEdgeVertexData[inputData["edges"] * scalingFactor];

If[FileExistsQ[outputFileName], DeleteFile[outputFileName] ];
Export[outputFileName, data];
Print["saved golden field data to " <> outputFileName];
