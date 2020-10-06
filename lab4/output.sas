begin_version
3
end_version
begin_metric
0
end_metric
5
begin_variable
var0
-1
3
Atom is-at(s, r1)
Atom is-at(s, r2)
Atom is-at(s, r3)
end_variable
begin_variable
var1
-1
3
Atom is-at(b, r1)
Atom is-at(b, r2)
Atom is-at(b, r3)
end_variable
begin_variable
var2
-1
2
Atom is-light-on(r3)
NegatedAtom is-light-on(r3)
end_variable
begin_variable
var3
-1
2
Atom is-light-on(r2)
NegatedAtom is-light-on(r2)
end_variable
begin_variable
var4
-1
2
Atom is-light-on(r1)
NegatedAtom is-light-on(r1)
end_variable
0
begin_state
1
0
1
1
1
end_state
begin_goal
3
2 0
3 0
4 0
end_goal
13
begin_operator
moves s r1 r2 d1
0
1
0 0 0 1
1
end_operator
begin_operator
moves s r2 r1 d1
0
1
0 0 1 0
1
end_operator
begin_operator
moves s r2 r3 d2
0
1
0 0 1 2
1
end_operator
begin_operator
moves s r2 r3 d3
0
1
0 0 1 2
1
end_operator
begin_operator
moves s r3 r2 d2
0
1
0 0 2 1
1
end_operator
begin_operator
moves s r3 r2 d3
0
1
0 0 2 1
1
end_operator
begin_operator
pushes_box s b r1 r2 d1
1
0 0
1
0 1 0 1
1
end_operator
begin_operator
pushes_box s b r2 r1 d1
1
0 1
1
0 1 1 0
1
end_operator
begin_operator
pushes_box s b r2 r3 d2
1
0 1
1
0 1 1 2
1
end_operator
begin_operator
pushes_box s b r3 r2 d2
1
0 2
1
0 1 2 1
1
end_operator
begin_operator
turns_lights_on s b r1
2
1 0
0 0
1
0 4 1 0
1
end_operator
begin_operator
turns_lights_on s b r2
2
1 1
0 1
1
0 3 1 0
1
end_operator
begin_operator
turns_lights_on s b r3
2
1 2
0 2
1
0 2 1 0
1
end_operator
0
