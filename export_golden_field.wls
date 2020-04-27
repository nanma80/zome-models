#!/usr/bin/env wolframscript

dataFolder = "data";
epsilon = 0.0000001;

level = 2;

shape = "nested_zome_ball_" <> IntegerString[level];
dataFileName = FileNameJoin[{dataFolder, "golden_field_" <> shape <> ".wl"}];

outputFileName = "golden_field_" <> shape <> ".json";
outputFileName = FileNameJoin[{dataFolder, outputFileName}];

Print["Reading data for " <> shape <> " from " <> dataFileName];

inputData = Get[dataFileName];

outputData = <|
	"vertices" -> inputData["vertices"],
	"edges" -> inputData["edges"] - 1
|>;

If[FileExistsQ[outputFileName], DeleteFile[outputFileName] ];
Export[outputFileName, outputData, "RawJSON", Compact -> True];
Print["saved json data to " <> outputFileName];
