#!/usr/bin/env wolframscript


dataFolder = "data";
AllCells = True;
epsilon = 0.0000001;
dataFileName = FileNameJoin[{dataFolder, "nested_zome_ball_1.wl"}];

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


phi = (1 + Sqrt[5])/2;

vertex1 = {0, 1, phi^2} + {1, 0, phi} * phi;

mirrors = {
    {0, 1, 0},
    {1, phi, -1/phi},
    {1, -phi, 1/phi}
};

edgeShort = {vertex1, reflect[vertex1, {0, 1, 0} ]};
edgeLong = {vertex1, reflect[vertex1, {1, 0, 0} ]};
rectFace = {vertex1, 
            reflect[vertex1, {0, 1, 0} ], 
            reflect[ reflect[vertex1, {0, 1, 0} ], {1, 0, 0}],
            reflect[vertex1, {1, 0, 0} ]
           };

edges = {edgeShort, edgeLong};
countStart = -1;
For[i = 0, i < 10 && countStart != Length[edges], i++,
    countStart = Length[edges];
    For[mirrorIndex = 3, mirrorIndex >= 1, mirrorIndex--,
        edges = Union[edges, Map[reflect[#, mirrors[[mirrorIndex]]]&, edges, {2}], 
            SameTest -> sameCenter];
    ];
    Print[{i, Length[edges]}];
];

vertices = Union[Flatten[edges, 1], SameTest -> sameVertex];

rectFaces = {rectFace};
countStart = -1;
For[i = 0, i < 10 && countStart != Length[rectFaces], i++,
    countStart = Length[rectFaces];
    For[mirrorIndex = 3, mirrorIndex >= 1, mirrorIndex--,
        rectFaces = Union[rectFaces, Map[reflect[#, mirrors[[mirrorIndex]]]&, rectFaces, {2}], 
            SameTest -> sameCenter];
    ];
    Print[{i, Length[rectFaces]}];
];


Print[edges//Length];
Print[vertices//Length];

data = <|
    "vertices" -> vertices,
    "edges" -> edges,
    "rectFaces" -> rectFaces
|>

If[FileExistsQ[dataFileName], DeleteFile[dataFileName]];
Save[dataFileName, data];
Print["Saved cells to " <> dataFileName];

(*
    phi = (1 + Sqrt[5])/2;
    max = 3 + Sqrt[5]; (* max coordinates in vertices *)
    f[d_] := (phi*d - 2 max)/(d - 2 max)
    sol = Solve[f[d] == phi^2, {d}][[1]]
    (* {d -> 4 (2 + Sqrt[5])} , 4 * phi^3 *)
    (d - 2 max)/.sol//Simplify
    (* 2(1 + Sqrt[5]), 4 phi *)

    ((phi*d - 2 max)/.sol)//Simplify
    (* 4 (2 + Sqrt[5]), 4 phi^3 *)

    (* original blue strut length, 2, 2*phi. need double length structs *)
*)
