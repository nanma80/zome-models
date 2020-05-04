

#declare           look_dir = <-0.92808427,-0.32173864,0.18746687>;

#declare             up_dir = <-0.34459728,0.55130211,-0.75981491>;

#declare          right_dir = <0.14111093,-0.76977285,-0.62252508>;

#declare viewpoint_distance = 108.73127;

#declare near_clip_distance = 0.2718281786416199;

#declare far_clip_distance = 217.46253967285156;

#declare      look_at_point = <0,0,0>;

#declare      field_of_view = 0.4426289;

#declare       canvas_width = 2000;

#declare      canvas_height = 2000;

#declare      parallel_proj = 0;

#declare anim = transform { rotate -58.283 * y rotate clock * 72 * x rotate 58.283 * y }global_settings { assumed_gamma 2.2 }camera {    #if ( parallel_proj )        orthographic    #end    location look_at_point - viewpoint_distance * look_dir    sky up_dir    right ( image_width / image_height ) * -x    up y    angle ( field_of_view / pi ) * 180    look_at look_at_point}#declare tau = ( 1 + sqrt(5) ) / 2;#declare phi = ( 1 + sqrt(5) ) / 2;#declare light_distance = viewpoint_distance * 2;#ifndef (multiplier_light_1)#declare multiplier_light_1 = 1;#end#ifndef (multiplier_light_2)#declare multiplier_light_2 = 1;#end#ifndef (multiplier_light_3)#declare multiplier_light_3 = 1;#end#ifndef (multiplier_ambient)#declare multiplier_ambient = 1.7;#end


light_source { -light_distance * <-0.44237605,-1.64281356,0.32475671> color rgb <0.92156863,0.92156863,0.89411765> * multiplier_light_1 }

light_source { -light_distance * <-0.14111094,0.76977283,0.6225251> color rgb <0.89411765,0.89411765,0.92156863> * multiplier_light_2 }

light_source { -light_distance * <-0.92808425,-0.32173863,0.18746687> color rgb <0.11764706,0.11764706,0.11764706> * multiplier_light_3 }

#declare ambient_color = color rgb <0.16078432,0.16078432,0.16078432>;

#default { texture { finish { phong 0.3 ambient multiplier_ambient * ambient_color diffuse 0.6 } } }

background { color rgb <0.68627453,0.78431374,0.86274511> }

#declare phi = ( 1 + sqrt(5) ) / 2;
#declare shape0 = union {
polygon {6, (<-1 +1/2*phi, -1, 1/2 -1/2*phi>)(<1/2 -1/2*phi, -1/2*phi, 1 -phi>)(<0, -3/2 +1/2*phi, -1/2*phi>)(<-1/2 +1/2*phi, -1/2*phi, 1 -phi>)(<1 -1/2*phi, -1, 1/2 -1/2*phi>)(<-1 +1/2*phi, -1, 1/2 -1/2*phi>)}
polygon {4, (<-1/2 +1/2*phi, -1/2*phi, -1 +phi>)(<1/2*phi, 1 -phi, -1/2 +1/2*phi>)(<-1 +phi, 1/2 -1/2*phi, 1/2*phi>)(<-1/2 +1/2*phi, -1/2*phi, -1 +phi>)}
polygon {4, (<0, 3/2 -1/2*phi, 1/2*phi>)(<1/2 -1/2*phi, 1 -1/2*phi, 1>)(<-1/2 +1/2*phi, 1 -1/2*phi, 1>)(<0, 3/2 -1/2*phi, 1/2*phi>)}
polygon {5, (<-1/2*phi, -1 +phi, -1/2 +1/2*phi>)(<-1, -1/2 +1/2*phi, 1 -1/2*phi>)(<-1/2*phi, 0, 3/2 -1/2*phi>)(<1 -phi, -1/2 +1/2*phi, 1/2*phi>)(<-1/2*phi, -1 +phi, -1/2 +1/2*phi>)}
polygon {4, (<-1, -1/2 +1/2*phi, -1 +1/2*phi>)(<-1/2*phi, 0, -3/2 +1/2*phi>)(<-1, 1/2 -1/2*phi, -1 +1/2*phi>)(<-1, -1/2 +1/2*phi, -1 +1/2*phi>)}
polygon {5, (<-1 +1/2*phi, -1, 1/2 -1/2*phi>)(<-3/2 +1/2*phi, -1/2*phi, 0>)(<-1/2*phi, 1 -phi, 1/2 -1/2*phi>)(<1/2 -1/2*phi, -1/2*phi, 1 -phi>)(<-1 +1/2*phi, -1, 1/2 -1/2*phi>)}
polygon {5, (<1/2 -1/2*phi, -1/2*phi, -1 +phi>)(<0, -3/2 +1/2*phi, 1/2*phi>)(<1/2 -1/2*phi, -1 +1/2*phi, 1>)(<1 -phi, 1/2 -1/2*phi, 1/2*phi>)(<1/2 -1/2*phi, -1/2*phi, -1 +phi>)}
polygon {6, (<1/2 -1/2*phi, 1/2*phi, -1 +phi>)(<0, 3/2 -1/2*phi, 1/2*phi>)(<-1/2 +1/2*phi, 1/2*phi, -1 +phi>)(<1 -1/2*phi, 1, -1/2 +1/2*phi>)(<-1 +1/2*phi, 1, -1/2 +1/2*phi>)(<1/2 -1/2*phi, 1/2*phi, -1 +phi>)}
polygon {5, (<1, -1/2 +1/2*phi, 1 -1/2*phi>)(<1/2*phi, -1 +phi, -1/2 +1/2*phi>)(<-1 +phi, -1/2 +1/2*phi, 1/2*phi>)(<1/2*phi, 0, 3/2 -1/2*phi>)(<1, -1/2 +1/2*phi, 1 -1/2*phi>)}
polygon {5, (<-1 +1/2*phi, 1, -1/2 +1/2*phi>)(<1 -1/2*phi, 1, -1/2 +1/2*phi>)(<1 -1/2*phi, 1, 1/2 -1/2*phi>)(<-1 +1/2*phi, 1, 1/2 -1/2*phi>)(<-1 +1/2*phi, 1, -1/2 +1/2*phi>)}
polygon {6, (<1/2 -1/2*phi, 1 -1/2*phi, -1>)(<1/2 -1/2*phi, -1 +1/2*phi, -1>)(<1 -phi, 1/2 -1/2*phi, -1/2*phi>)(<-1/2*phi, 0, -3/2 +1/2*phi>)(<1 -phi, -1/2 +1/2*phi, -1/2*phi>)(<1/2 -1/2*phi, 1 -1/2*phi, -1>)}
polygon {5, (<1/2 -1/2*phi, 1/2*phi, -1 +phi>)(<1 -phi, -1/2 +1/2*phi, 1/2*phi>)(<1/2 -1/2*phi, 1 -1/2*phi, 1>)(<0, 3/2 -1/2*phi, 1/2*phi>)(<1/2 -1/2*phi, 1/2*phi, -1 +phi>)}
polygon {4, (<-1/2*phi, -1 +phi, 1/2 -1/2*phi>)(<1/2 -1/2*phi, 1/2*phi, 1 -phi>)(<1 -phi, -1/2 +1/2*phi, -1/2*phi>)(<-1/2*phi, -1 +phi, 1/2 -1/2*phi>)}
polygon {5, (<-1/2 +1/2*phi, 1 -1/2*phi, -1>)(<-1/2 +1/2*phi, -1 +1/2*phi, -1>)(<1/2 -1/2*phi, -1 +1/2*phi, -1>)(<1/2 -1/2*phi, 1 -1/2*phi, -1>)(<-1/2 +1/2*phi, 1 -1/2*phi, -1>)}
polygon {4, (<1/2 -1/2*phi, -1/2*phi, 1 -phi>)(<-1/2*phi, 1 -phi, 1/2 -1/2*phi>)(<1 -phi, 1/2 -1/2*phi, -1/2*phi>)(<1/2 -1/2*phi, -1/2*phi, 1 -phi>)}
polygon {5, (<-1/2*phi, 1 -phi, -1/2 +1/2*phi>)(<1 -phi, 1/2 -1/2*phi, 1/2*phi>)(<-1/2*phi, 0, 3/2 -1/2*phi>)(<-1, 1/2 -1/2*phi, 1 -1/2*phi>)(<-1/2*phi, 1 -phi, -1/2 +1/2*phi>)}
polygon {5, (<1 -1/2*phi, -1, -1/2 +1/2*phi>)(<-1 +1/2*phi, -1, -1/2 +1/2*phi>)(<-1 +1/2*phi, -1, 1/2 -1/2*phi>)(<1 -1/2*phi, -1, 1/2 -1/2*phi>)(<1 -1/2*phi, -1, -1/2 +1/2*phi>)}
polygon {5, (<-1, -1/2 +1/2*phi, 1 -1/2*phi>)(<-1, -1/2 +1/2*phi, -1 +1/2*phi>)(<-1, 1/2 -1/2*phi, -1 +1/2*phi>)(<-1, 1/2 -1/2*phi, 1 -1/2*phi>)(<-1, -1/2 +1/2*phi, 1 -1/2*phi>)}
polygon {5, (<-3/2 +1/2*phi, 1/2*phi, 0>)(<-1 +1/2*phi, 1, 1/2 -1/2*phi>)(<1/2 -1/2*phi, 1/2*phi, 1 -phi>)(<-1/2*phi, -1 +phi, 1/2 -1/2*phi>)(<-3/2 +1/2*phi, 1/2*phi, 0>)}
polygon {4, (<1/2*phi, -1 +phi, -1/2 +1/2*phi>)(<-1/2 +1/2*phi, 1/2*phi, -1 +phi>)(<-1 +phi, -1/2 +1/2*phi, 1/2*phi>)(<1/2*phi, -1 +phi, -1/2 +1/2*phi>)}
polygon {6, (<0, 3/2 -1/2*phi, -1/2*phi>)(<1/2 -1/2*phi, 1/2*phi, 1 -phi>)(<-1 +1/2*phi, 1, 1/2 -1/2*phi>)(<1 -1/2*phi, 1, 1/2 -1/2*phi>)(<-1/2 +1/2*phi, 1/2*phi, 1 -phi>)(<0, 3/2 -1/2*phi, -1/2*phi>)}
polygon {5, (<0, 3/2 -1/2*phi, -1/2*phi>)(<1/2 -1/2*phi, 1 -1/2*phi, -1>)(<1 -phi, -1/2 +1/2*phi, -1/2*phi>)(<1/2 -1/2*phi, 1/2*phi, 1 -phi>)(<0, 3/2 -1/2*phi, -1/2*phi>)}
polygon {6, (<-3/2 +1/2*phi, 1/2*phi, 0>)(<-1/2*phi, -1 +phi, 1/2 -1/2*phi>)(<-1, -1/2 +1/2*phi, -1 +1/2*phi>)(<-1, -1/2 +1/2*phi, 1 -1/2*phi>)(<-1/2*phi, -1 +phi, -1/2 +1/2*phi>)(<-3/2 +1/2*phi, 1/2*phi, 0>)}
polygon {4, (<1, 1/2 -1/2*phi, -1 +1/2*phi>)(<1/2*phi, 0, -3/2 +1/2*phi>)(<1, -1/2 +1/2*phi, -1 +1/2*phi>)(<1, 1/2 -1/2*phi, -1 +1/2*phi>)}
polygon {4, (<1, -1/2 +1/2*phi, 1 -1/2*phi>)(<1/2*phi, 0, 3/2 -1/2*phi>)(<1, 1/2 -1/2*phi, 1 -1/2*phi>)(<1, -1/2 +1/2*phi, 1 -1/2*phi>)}
polygon {5, (<1/2*phi, 1 -phi, -1/2 +1/2*phi>)(<1, 1/2 -1/2*phi, 1 -1/2*phi>)(<1/2*phi, 0, 3/2 -1/2*phi>)(<-1 +phi, 1/2 -1/2*phi, 1/2*phi>)(<1/2*phi, 1 -phi, -1/2 +1/2*phi>)}
polygon {4, (<-1 +1/2*phi, -1, -1/2 +1/2*phi>)(<-3/2 +1/2*phi, -1/2*phi, 0>)(<-1 +1/2*phi, -1, 1/2 -1/2*phi>)(<-1 +1/2*phi, -1, -1/2 +1/2*phi>)}
polygon {5, (<1, 1/2 -1/2*phi, -1 +1/2*phi>)(<1/2*phi, 1 -phi, 1/2 -1/2*phi>)(<-1 +phi, 1/2 -1/2*phi, -1/2*phi>)(<1/2*phi, 0, -3/2 +1/2*phi>)(<1, 1/2 -1/2*phi, -1 +1/2*phi>)}
polygon {4, (<-1/2 +1/2*phi, 1/2*phi, 1 -phi>)(<1/2*phi, -1 +phi, 1/2 -1/2*phi>)(<-1 +phi, -1/2 +1/2*phi, -1/2*phi>)(<-1/2 +1/2*phi, 1/2*phi, 1 -phi>)}
polygon {6, (<-1/2 +1/2*phi, -1/2*phi, -1 +phi>)(<0, -3/2 +1/2*phi, 1/2*phi>)(<1/2 -1/2*phi, -1/2*phi, -1 +phi>)(<-1 +1/2*phi, -1, -1/2 +1/2*phi>)(<1 -1/2*phi, -1, -1/2 +1/2*phi>)(<-1/2 +1/2*phi, -1/2*phi, -1 +phi>)}
polygon {4, (<1/2*phi, 1 -phi, 1/2 -1/2*phi>)(<-1/2 +1/2*phi, -1/2*phi, 1 -phi>)(<-1 +phi, 1/2 -1/2*phi, -1/2*phi>)(<1/2*phi, 1 -phi, 1/2 -1/2*phi>)}
polygon {5, (<0, -3/2 +1/2*phi, -1/2*phi>)(<1/2 -1/2*phi, -1/2*phi, 1 -phi>)(<1 -phi, 1/2 -1/2*phi, -1/2*phi>)(<1/2 -1/2*phi, -1 +1/2*phi, -1>)(<0, -3/2 +1/2*phi, -1/2*phi>)}
polygon {5, (<-1/2 +1/2*phi, -1/2*phi, -1 +phi>)(<1 -1/2*phi, -1, -1/2 +1/2*phi>)(<3/2 -1/2*phi, -1/2*phi, 0>)(<1/2*phi, 1 -phi, -1/2 +1/2*phi>)(<-1/2 +1/2*phi, -1/2*phi, -1 +phi>)}
polygon {5, (<0, 3/2 -1/2*phi, -1/2*phi>)(<-1/2 +1/2*phi, 1/2*phi, 1 -phi>)(<-1 +phi, -1/2 +1/2*phi, -1/2*phi>)(<-1/2 +1/2*phi, 1 -1/2*phi, -1>)(<0, 3/2 -1/2*phi, -1/2*phi>)}
polygon {6, (<1, -1/2 +1/2*phi, 1 -1/2*phi>)(<1, -1/2 +1/2*phi, -1 +1/2*phi>)(<1/2*phi, -1 +phi, 1/2 -1/2*phi>)(<3/2 -1/2*phi, 1/2*phi, 0>)(<1/2*phi, -1 +phi, -1/2 +1/2*phi>)(<1, -1/2 +1/2*phi, 1 -1/2*phi>)}
polygon {4, (<1 -1/2*phi, -1, -1/2 +1/2*phi>)(<1 -1/2*phi, -1, 1/2 -1/2*phi>)(<3/2 -1/2*phi, -1/2*phi, 0>)(<1 -1/2*phi, -1, -1/2 +1/2*phi>)}
polygon {5, (<-1, -1/2 +1/2*phi, -1 +1/2*phi>)(<-1/2*phi, -1 +phi, 1/2 -1/2*phi>)(<1 -phi, -1/2 +1/2*phi, -1/2*phi>)(<-1/2*phi, 0, -3/2 +1/2*phi>)(<-1, -1/2 +1/2*phi, -1 +1/2*phi>)}
polygon {5, (<1, -1/2 +1/2*phi, 1 -1/2*phi>)(<1, 1/2 -1/2*phi, 1 -1/2*phi>)(<1, 1/2 -1/2*phi, -1 +1/2*phi>)(<1, -1/2 +1/2*phi, -1 +1/2*phi>)(<1, -1/2 +1/2*phi, 1 -1/2*phi>)}
polygon {5, (<1/2*phi, 0, -3/2 +1/2*phi>)(<-1 +phi, -1/2 +1/2*phi, -1/2*phi>)(<1/2*phi, -1 +phi, 1/2 -1/2*phi>)(<1, -1/2 +1/2*phi, -1 +1/2*phi>)(<1/2*phi, 0, -3/2 +1/2*phi>)}
polygon {5, (<-1/2 +1/2*phi, -1/2*phi, -1 +phi>)(<-1 +phi, 1/2 -1/2*phi, 1/2*phi>)(<-1/2 +1/2*phi, -1 +1/2*phi, 1>)(<0, -3/2 +1/2*phi, 1/2*phi>)(<-1/2 +1/2*phi, -1/2*phi, -1 +phi>)}
polygon {5, (<-1/2 +1/2*phi, 1 -1/2*phi, 1>)(<1/2 -1/2*phi, 1 -1/2*phi, 1>)(<1/2 -1/2*phi, -1 +1/2*phi, 1>)(<-1/2 +1/2*phi, -1 +1/2*phi, 1>)(<-1/2 +1/2*phi, 1 -1/2*phi, 1>)}
polygon {6, (<-1/2*phi, 1 -phi, -1/2 +1/2*phi>)(<-1, 1/2 -1/2*phi, 1 -1/2*phi>)(<-1, 1/2 -1/2*phi, -1 +1/2*phi>)(<-1/2*phi, 1 -phi, 1/2 -1/2*phi>)(<-3/2 +1/2*phi, -1/2*phi, 0>)(<-1/2*phi, 1 -phi, -1/2 +1/2*phi>)}
polygon {4, (<0, 3/2 -1/2*phi, -1/2*phi>)(<-1/2 +1/2*phi, 1 -1/2*phi, -1>)(<1/2 -1/2*phi, 1 -1/2*phi, -1>)(<0, 3/2 -1/2*phi, -1/2*phi>)}
polygon {5, (<-1, 1/2 -1/2*phi, -1 +1/2*phi>)(<-1/2*phi, 0, -3/2 +1/2*phi>)(<1 -phi, 1/2 -1/2*phi, -1/2*phi>)(<-1/2*phi, 1 -phi, 1/2 -1/2*phi>)(<-1, 1/2 -1/2*phi, -1 +1/2*phi>)}
polygon {5, (<1/2*phi, -1 +phi, -1/2 +1/2*phi>)(<3/2 -1/2*phi, 1/2*phi, 0>)(<1 -1/2*phi, 1, -1/2 +1/2*phi>)(<-1/2 +1/2*phi, 1/2*phi, -1 +phi>)(<1/2*phi, -1 +phi, -1/2 +1/2*phi>)}
polygon {4, (<-1/2*phi, -1 +phi, -1/2 +1/2*phi>)(<1 -phi, -1/2 +1/2*phi, 1/2*phi>)(<1/2 -1/2*phi, 1/2*phi, -1 +phi>)(<-1/2*phi, -1 +phi, -1/2 +1/2*phi>)}
polygon {6, (<-1 +phi, 1/2 -1/2*phi, -1/2*phi>)(<-1/2 +1/2*phi, -1 +1/2*phi, -1>)(<-1/2 +1/2*phi, 1 -1/2*phi, -1>)(<-1 +phi, -1/2 +1/2*phi, -1/2*phi>)(<1/2*phi, 0, -3/2 +1/2*phi>)(<-1 +phi, 1/2 -1/2*phi, -1/2*phi>)}
polygon {5, (<-1 +1/2*phi, -1, -1/2 +1/2*phi>)(<1/2 -1/2*phi, -1/2*phi, -1 +phi>)(<-1/2*phi, 1 -phi, -1/2 +1/2*phi>)(<-3/2 +1/2*phi, -1/2*phi, 0>)(<-1 +1/2*phi, -1, -1/2 +1/2*phi>)}
polygon {4, (<3/2 -1/2*phi, 1/2*phi, 0>)(<1 -1/2*phi, 1, 1/2 -1/2*phi>)(<1 -1/2*phi, 1, -1/2 +1/2*phi>)(<3/2 -1/2*phi, 1/2*phi, 0>)}
polygon {4, (<-3/2 +1/2*phi, 1/2*phi, 0>)(<-1 +1/2*phi, 1, -1/2 +1/2*phi>)(<-1 +1/2*phi, 1, 1/2 -1/2*phi>)(<-3/2 +1/2*phi, 1/2*phi, 0>)}
polygon {6, (<-1/2*phi, 0, 3/2 -1/2*phi>)(<1 -phi, 1/2 -1/2*phi, 1/2*phi>)(<1/2 -1/2*phi, -1 +1/2*phi, 1>)(<1/2 -1/2*phi, 1 -1/2*phi, 1>)(<1 -phi, -1/2 +1/2*phi, 1/2*phi>)(<-1/2*phi, 0, 3/2 -1/2*phi>)}
polygon {4, (<1/2 -1/2*phi, -1/2*phi, -1 +phi>)(<1 -phi, 1/2 -1/2*phi, 1/2*phi>)(<-1/2*phi, 1 -phi, -1/2 +1/2*phi>)(<1/2 -1/2*phi, -1/2*phi, -1 +phi>)}
polygon {5, (<-3/2 +1/2*phi, 1/2*phi, 0>)(<-1/2*phi, -1 +phi, -1/2 +1/2*phi>)(<1/2 -1/2*phi, 1/2*phi, -1 +phi>)(<-1 +1/2*phi, 1, -1/2 +1/2*phi>)(<-3/2 +1/2*phi, 1/2*phi, 0>)}
polygon {5, (<-1/2 +1/2*phi, 1/2*phi, 1 -phi>)(<1 -1/2*phi, 1, 1/2 -1/2*phi>)(<3/2 -1/2*phi, 1/2*phi, 0>)(<1/2*phi, -1 +phi, 1/2 -1/2*phi>)(<-1/2 +1/2*phi, 1/2*phi, 1 -phi>)}
polygon {5, (<-1 +phi, 1/2 -1/2*phi, -1/2*phi>)(<-1/2 +1/2*phi, -1/2*phi, 1 -phi>)(<0, -3/2 +1/2*phi, -1/2*phi>)(<-1/2 +1/2*phi, -1 +1/2*phi, -1>)(<-1 +phi, 1/2 -1/2*phi, -1/2*phi>)}
polygon {5, (<1 -1/2*phi, -1, 1/2 -1/2*phi>)(<-1/2 +1/2*phi, -1/2*phi, 1 -phi>)(<1/2*phi, 1 -phi, 1/2 -1/2*phi>)(<3/2 -1/2*phi, -1/2*phi, 0>)(<1 -1/2*phi, -1, 1/2 -1/2*phi>)}
polygon {6, (<-1 +phi, 1/2 -1/2*phi, 1/2*phi>)(<1/2*phi, 0, 3/2 -1/2*phi>)(<-1 +phi, -1/2 +1/2*phi, 1/2*phi>)(<-1/2 +1/2*phi, 1 -1/2*phi, 1>)(<-1/2 +1/2*phi, -1 +1/2*phi, 1>)(<-1 +phi, 1/2 -1/2*phi, 1/2*phi>)}
polygon {4, (<-1, -1/2 +1/2*phi, 1 -1/2*phi>)(<-1, 1/2 -1/2*phi, 1 -1/2*phi>)(<-1/2*phi, 0, 3/2 -1/2*phi>)(<-1, -1/2 +1/2*phi, 1 -1/2*phi>)}
polygon {5, (<-1 +phi, -1/2 +1/2*phi, 1/2*phi>)(<-1/2 +1/2*phi, 1/2*phi, -1 +phi>)(<0, 3/2 -1/2*phi, 1/2*phi>)(<-1/2 +1/2*phi, 1 -1/2*phi, 1>)(<-1 +phi, -1/2 +1/2*phi, 1/2*phi>)}
polygon {6, (<1/2*phi, 1 -phi, -1/2 +1/2*phi>)(<3/2 -1/2*phi, -1/2*phi, 0>)(<1/2*phi, 1 -phi, 1/2 -1/2*phi>)(<1, 1/2 -1/2*phi, -1 +1/2*phi>)(<1, 1/2 -1/2*phi, 1 -1/2*phi>)(<1/2*phi, 1 -phi, -1/2 +1/2*phi>)}
polygon {4, (<-1/2 +1/2*phi, -1 +1/2*phi, -1>)(<0, -3/2 +1/2*phi, -1/2*phi>)(<1/2 -1/2*phi, -1 +1/2*phi, -1>)(<-1/2 +1/2*phi, -1 +1/2*phi, -1>)}
polygon {4, (<0, -3/2 +1/2*phi, 1/2*phi>)(<-1/2 +1/2*phi, -1 +1/2*phi, 1>)(<1/2 -1/2*phi, -1 +1/2*phi, 1>)(<0, -3/2 +1/2*phi, 1/2*phi>)}
}
#declare trans0 = transform { matrix < 1, 0, 0, 0, 1, 0, 0, 0, 1,  0, 0, 0 > }
#declare color_255_255_255 = texture { pigment { color rgb <1,1,1> } };
#declare shape1 = union {
polygon {5, (<1 +2*phi, -1/2 +1/2*phi, -1 +1/2*phi>)(<1 +2*phi, 1/2 -1/2*phi, -1 +1/2*phi>)(<1, 1/2 -1/2*phi, -1 +1/2*phi>)(<1, -1/2 +1/2*phi, -1 +1/2*phi>)(<1 +2*phi, -1/2 +1/2*phi, -1 +1/2*phi>)}
polygon {5, (<1 +2*phi, -1/2 +1/2*phi, 1 -1/2*phi>)(<1 +2*phi, -1/2 +1/2*phi, -1 +1/2*phi>)(<1, -1/2 +1/2*phi, -1 +1/2*phi>)(<1, -1/2 +1/2*phi, 1 -1/2*phi>)(<1 +2*phi, -1/2 +1/2*phi, 1 -1/2*phi>)}
polygon {5, (<1 +2*phi, 1/2 -1/2*phi, 1 -1/2*phi>)(<1, 1/2 -1/2*phi, 1 -1/2*phi>)(<1, 1/2 -1/2*phi, -1 +1/2*phi>)(<1 +2*phi, 1/2 -1/2*phi, -1 +1/2*phi>)(<1 +2*phi, 1/2 -1/2*phi, 1 -1/2*phi>)}
polygon {5, (<1 +2*phi, -1/2 +1/2*phi, 1 -1/2*phi>)(<1, -1/2 +1/2*phi, 1 -1/2*phi>)(<1, 1/2 -1/2*phi, 1 -1/2*phi>)(<1 +2*phi, 1/2 -1/2*phi, 1 -1/2*phi>)(<1 +2*phi, -1/2 +1/2*phi, 1 -1/2*phi>)}
}
#declare trans1 = transform { matrix < 0, 0, -1, -1, 0, 0, 0, 1, 0,  0, 0, 0 > }
#declare color_0_118_149 = texture { pigment { color rgb <0,0.463,0.584> } };
#declare shape2 = union {
polygon {4, (<3/2 +2*phi, -1/2 +1/2*phi, -phi>)(<3/2 +2*phi, 1/2 -1/2*phi, -phi>)(<5/2 +5/2*phi, 0, -1/2 -phi>)(<3/2 +2*phi, -1/2 +1/2*phi, -phi>)}
polygon {5, (<2 +5*phi, -1/2 +1/2*phi, -5/2*phi>)(<2 +5*phi, 1/2 -1/2*phi, -5/2*phi>)(<3/2 +3*phi, 1/2 -1/2*phi, -1 -phi>)(<3/2 +3*phi, -1/2 +1/2*phi, -1 -phi>)(<2 +5*phi, -1/2 +1/2*phi, -5/2*phi>)}
polygon {4, (<3/2 +2*phi, -1/2 +1/2*phi, -phi>)(<5/2 +5/2*phi, 0, -1/2 -phi>)(<3/2 +3*phi, -1/2 +1/2*phi, -1 -phi>)(<3/2 +2*phi, -1/2 +1/2*phi, -phi>)}
polygon {4, (<1/2 +5/2*phi, 0, -1/2 -phi>)(<3/2 +3*phi, 1/2 -1/2*phi, -1 -phi>)(<3/2 +2*phi, 1/2 -1/2*phi, -phi>)(<1/2 +5/2*phi, 0, -1/2 -phi>)}
polygon {4, (<1/2 +5/2*phi, 0, -1/2 -phi>)(<3/2 +3*phi, -1/2 +1/2*phi, -1 -phi>)(<3/2 +3*phi, 1/2 -1/2*phi, -1 -phi>)(<1/2 +5/2*phi, 0, -1/2 -phi>)}
polygon {5, (<1/2*phi, 0, -3/2 +1/2*phi>)(<1, -1/2 +1/2*phi, -1 +1/2*phi>)(<3/2 +2*phi, -1/2 +1/2*phi, -phi>)(<1/2 +5/2*phi, 0, -1/2 -phi>)(<1/2*phi, 0, -3/2 +1/2*phi>)}
polygon {4, (<3/2 +2*phi, 1/2 -1/2*phi, -phi>)(<3/2 +3*phi, 1/2 -1/2*phi, -1 -phi>)(<5/2 +5/2*phi, 0, -1/2 -phi>)(<3/2 +2*phi, 1/2 -1/2*phi, -phi>)}
polygon {5, (<5/2 +5/2*phi, 0, -1/2 -phi>)(<3/2 +3*phi, 1/2 -1/2*phi, -1 -phi>)(<2 +5*phi, 1/2 -1/2*phi, -5/2*phi>)(<3 +9/2*phi, 0, 1/2 -5/2*phi>)(<5/2 +5/2*phi, 0, -1/2 -phi>)}
polygon {4, (<3/2 +2*phi, -1/2 +1/2*phi, -phi>)(<3/2 +3*phi, -1/2 +1/2*phi, -1 -phi>)(<1/2 +5/2*phi, 0, -1/2 -phi>)(<3/2 +2*phi, -1/2 +1/2*phi, -phi>)}
polygon {5, (<1/2*phi, 0, -3/2 +1/2*phi>)(<1/2 +5/2*phi, 0, -1/2 -phi>)(<3/2 +2*phi, 1/2 -1/2*phi, -phi>)(<1, 1/2 -1/2*phi, -1 +1/2*phi>)(<1/2*phi, 0, -3/2 +1/2*phi>)}
polygon {5, (<1, -1/2 +1/2*phi, -1 +1/2*phi>)(<1, 1/2 -1/2*phi, -1 +1/2*phi>)(<3/2 +2*phi, 1/2 -1/2*phi, -phi>)(<3/2 +2*phi, -1/2 +1/2*phi, -phi>)(<1, -1/2 +1/2*phi, -1 +1/2*phi>)}
polygon {5, (<2 +5*phi, -1/2 +1/2*phi, -5/2*phi>)(<3/2 +3*phi, -1/2 +1/2*phi, -1 -phi>)(<5/2 +5/2*phi, 0, -1/2 -phi>)(<3 +9/2*phi, 0, 1/2 -5/2*phi>)(<2 +5*phi, -1/2 +1/2*phi, -5/2*phi>)}
}
#declare trans2 = transform { matrix < 0, 0, 1, 1, 0, 0, 0, 1, 0,  0, 0, 0 > }
#declare color_240_160_0 = texture { pigment { color rgb <0.941,0.627,0> } };
#declare shape3 = union {
polygon {5, (<1 +4*phi, -1/2 +1/2*phi, -1 +1/2*phi>)(<1 +4*phi, 1/2 -1/2*phi, -1 +1/2*phi>)(<1, 1/2 -1/2*phi, -1 +1/2*phi>)(<1, -1/2 +1/2*phi, -1 +1/2*phi>)(<1 +4*phi, -1/2 +1/2*phi, -1 +1/2*phi>)}
polygon {5, (<1 +4*phi, -1/2 +1/2*phi, 1 -1/2*phi>)(<1 +4*phi, -1/2 +1/2*phi, -1 +1/2*phi>)(<1, -1/2 +1/2*phi, -1 +1/2*phi>)(<1, -1/2 +1/2*phi, 1 -1/2*phi>)(<1 +4*phi, -1/2 +1/2*phi, 1 -1/2*phi>)}
polygon {5, (<1 +4*phi, 1/2 -1/2*phi, 1 -1/2*phi>)(<1, 1/2 -1/2*phi, 1 -1/2*phi>)(<1, 1/2 -1/2*phi, -1 +1/2*phi>)(<1 +4*phi, 1/2 -1/2*phi, -1 +1/2*phi>)(<1 +4*phi, 1/2 -1/2*phi, 1 -1/2*phi>)}
polygon {5, (<1 +4*phi, -1/2 +1/2*phi, 1 -1/2*phi>)(<1, -1/2 +1/2*phi, 1 -1/2*phi>)(<1, 1/2 -1/2*phi, 1 -1/2*phi>)(<1 +4*phi, 1/2 -1/2*phi, 1 -1/2*phi>)(<1 +4*phi, -1/2 +1/2*phi, 1 -1/2*phi>)}
}
#declare trans3 = transform { matrix < -1/2*phi, 1/2 -1/2*phi, 1/2, -1/2 +1/2*phi, 1/2, 1/2*phi, -1/2, 1/2*phi, 1/2 -1/2*phi,  0, 0, 0 > }
#declare trans4 = transform { matrix < 0, 1, 0, 0, 0, 1, 1, 0, 0,  0, 0, 0 > }
#declare trans5 = transform { matrix < -1/2*phi, -1/2 +1/2*phi, 1/2, -1/2 +1/2*phi, -1/2, 1/2*phi, 1/2, 1/2*phi, -1/2 +1/2*phi,  0, 0, 0 > }
#declare shape4 = union {
polygon {5, (<5 +10*phi, -1/2 +1/2*phi, -1 +1/2*phi>)(<5 +10*phi, 1/2 -1/2*phi, -1 +1/2*phi>)(<1, 1/2 -1/2*phi, -1 +1/2*phi>)(<1, -1/2 +1/2*phi, -1 +1/2*phi>)(<5 +10*phi, -1/2 +1/2*phi, -1 +1/2*phi>)}
polygon {5, (<5 +10*phi, -1/2 +1/2*phi, 1 -1/2*phi>)(<5 +10*phi, -1/2 +1/2*phi, -1 +1/2*phi>)(<1, -1/2 +1/2*phi, -1 +1/2*phi>)(<1, -1/2 +1/2*phi, 1 -1/2*phi>)(<5 +10*phi, -1/2 +1/2*phi, 1 -1/2*phi>)}
polygon {5, (<5 +10*phi, 1/2 -1/2*phi, 1 -1/2*phi>)(<1, 1/2 -1/2*phi, 1 -1/2*phi>)(<1, 1/2 -1/2*phi, -1 +1/2*phi>)(<5 +10*phi, 1/2 -1/2*phi, -1 +1/2*phi>)(<5 +10*phi, 1/2 -1/2*phi, 1 -1/2*phi>)}
polygon {5, (<5 +10*phi, -1/2 +1/2*phi, 1 -1/2*phi>)(<1, -1/2 +1/2*phi, 1 -1/2*phi>)(<1, 1/2 -1/2*phi, 1 -1/2*phi>)(<5 +10*phi, 1/2 -1/2*phi, 1 -1/2*phi>)(<5 +10*phi, -1/2 +1/2*phi, 1 -1/2*phi>)}
}
#declare trans6 = transform { matrix < 1/2*phi, -1/2 +1/2*phi, 1/2, 1/2 -1/2*phi, -1/2, 1/2*phi, 1/2, -1/2*phi, 1/2 -1/2*phi,  0, 0, 0 > }
#declare trans7 = transform { matrix < -1/2 +1/2*phi, 1/2, -1/2*phi, -1/2, 1/2*phi, -1/2 +1/2*phi, 1/2*phi, -1/2 +1/2*phi, 1/2,  0, 0, 0 > }
#declare trans8 = transform { matrix < -1/2, 1/2*phi, 1/2 -1/2*phi, -1/2*phi, 1/2 -1/2*phi, 1/2, -1/2 +1/2*phi, 1/2, 1/2*phi,  0, 0, 0 > }
#declare trans9 = transform { matrix < 1/2 -1/2*phi, -1/2, 1/2*phi, 1/2, -1/2*phi, 1/2 -1/2*phi, 1/2*phi, -1/2 +1/2*phi, 1/2,  0, 0, 0 > }
#declare trans10 = transform { matrix < 1/2*phi, 1/2 -1/2*phi, -1/2, 1/2 -1/2*phi, 1/2, -1/2*phi, 1/2, 1/2*phi, -1/2 +1/2*phi,  0, 0, 0 > }
#declare trans11 = transform { matrix < 1/2, -1/2*phi, 1/2 -1/2*phi, 1/2*phi, -1/2 +1/2*phi, 1/2, 1/2 -1/2*phi, -1/2, 1/2*phi,  0, 0, 0 > }
#declare trans12 = transform { matrix < 1/2, -1/2*phi, -1/2 +1/2*phi, 1/2*phi, -1/2 +1/2*phi, -1/2, -1/2 +1/2*phi, 1/2, 1/2*phi,  0, 0, 0 > }
#declare trans13 = transform { matrix < -1/2, -1/2*phi, -1/2 +1/2*phi, -1/2*phi, -1/2 +1/2*phi, -1/2, -1/2 +1/2*phi, -1/2, -1/2*phi,  0, 0, 0 > }
#declare trans14 = transform { matrix < -1/2, 1/2*phi, -1/2 +1/2*phi, 1/2*phi, -1/2 +1/2*phi, 1/2, -1/2 +1/2*phi, 1/2, -1/2*phi,  0, 0, 0 > }
#declare trans15 = transform { matrix < 0, -1, 0, 0, 0, -1, 1, 0, 0,  0, 0, 0 > }
#declare trans16 = transform { matrix < -1/2, -1/2*phi, 1/2 -1/2*phi, -1/2*phi, -1/2 +1/2*phi, 1/2, 1/2 -1/2*phi, 1/2, -1/2*phi,  0, 0, 0 > }
#declare trans17 = transform { matrix < 1/2, 1/2*phi, -1/2 +1/2*phi, 1/2*phi, 1/2 -1/2*phi, -1/2, 1/2 -1/2*phi, 1/2, -1/2*phi,  0, 0, 0 > }
#declare trans18 = transform { matrix < 1/2*phi, 1/2 -1/2*phi, 1/2, 1/2 -1/2*phi, 1/2, 1/2*phi, -1/2, -1/2*phi, -1/2 +1/2*phi,  0, 0, 0 > }
#declare trans19 = transform { matrix < -1/2, 1/2*phi, -1/2 +1/2*phi, -1/2*phi, 1/2 -1/2*phi, -1/2, 1/2 -1/2*phi, -1/2, 1/2*phi,  0, 0, 0 > }
#declare trans20 = transform { matrix < 1/2, 1/2*phi, 1/2 -1/2*phi, 1/2*phi, 1/2 -1/2*phi, 1/2, -1/2 +1/2*phi, -1/2, -1/2*phi,  0, 0, 0 > }
#declare trans21 = transform { matrix < 1/2 -1/2*phi, -1/2, -1/2*phi, 1/2, -1/2*phi, -1/2 +1/2*phi, -1/2*phi, 1/2 -1/2*phi, 1/2,  0, 0, 0 > }
#declare trans22 = transform { matrix < -1, 0, 0, 0, 1, 0, 0, 0, -1,  0, 0, 0 > }
#declare trans23 = transform { matrix < -1/2*phi, -1/2 +1/2*phi, -1/2, -1/2 +1/2*phi, -1/2, -1/2*phi, -1/2, -1/2*phi, -1/2 +1/2*phi,  0, 0, 0 > }
#declare trans24 = transform { matrix < 1/2 -1/2*phi, 1/2, -1/2*phi, 1/2, 1/2*phi, -1/2 +1/2*phi, 1/2*phi, 1/2 -1/2*phi, -1/2,  0, 0, 0 > }
#declare trans25 = transform { matrix < -1/2 +1/2*phi, -1/2, -1/2*phi, -1/2, -1/2*phi, -1/2 +1/2*phi, -1/2*phi, -1/2 +1/2*phi, -1/2,  0, 0, 0 > }
#declare trans26 = transform { matrix < -1/2 +1/2*phi, -1/2, 1/2*phi, 1/2, 1/2*phi, -1/2 +1/2*phi, -1/2*phi, -1/2 +1/2*phi, 1/2,  0, 0, 0 > }
#declare trans27 = transform { matrix < -1, 0, 0, 0, -1, 0, 0, 0, 1,  0, 0, 0 > }
#declare trans28 = transform { matrix < -1/2 +1/2*phi, -1/2, 1/2*phi, -1/2, -1/2*phi, 1/2 -1/2*phi, 1/2*phi, 1/2 -1/2*phi, -1/2,  0, 0, 0 > }
#declare trans29 = transform { matrix < 0, 0, -1, 1, 0, 0, 0, -1, 0,  0, 0, 0 > }
#declare trans30 = transform { matrix < -1/2 +1/2*phi, 1/2, 1/2*phi, -1/2, 1/2*phi, 1/2 -1/2*phi, -1/2*phi, 1/2 -1/2*phi, 1/2,  0, 0, 0 > }
#declare trans31 = transform { matrix < 1/2*phi, -1/2 +1/2*phi, -1/2, -1/2 +1/2*phi, 1/2, 1/2*phi, 1/2, -1/2*phi, -1/2 +1/2*phi,  0, 0, 0 > }
#declare trans32 = transform { matrix < -1/2*phi, 1/2 -1/2*phi, -1/2, -1/2 +1/2*phi, 1/2, -1/2*phi, 1/2, -1/2*phi, 1/2 -1/2*phi,  0, 0, 0 > }
#declare trans33 = transform { matrix < 1/2 -1/2*phi, 1/2, 1/2*phi, 1/2, 1/2*phi, 1/2 -1/2*phi, -1/2*phi, -1/2 +1/2*phi, -1/2,  0, 0, 0 > }
#declare trans34 = transform { matrix < -1/2, -1/2*phi, 1/2 -1/2*phi, 1/2*phi, 1/2 -1/2*phi, -1/2, -1/2 +1/2*phi, -1/2, 1/2*phi,  0, 0, 0 > }
#declare trans35 = transform { matrix < 1/2*phi, -1/2 +1/2*phi, -1/2, 1/2 -1/2*phi, -1/2, -1/2*phi, -1/2, 1/2*phi, 1/2 -1/2*phi,  0, 0, 0 > }
object { shape0 transform trans0 translate (<-2 -3*phi, 0, -3 -5*phi>) transform anim texture { color_255_255_255 } }
object { shape0 transform trans0 translate (<-2 -3*phi, 5 +8*phi, 0>) transform anim texture { color_255_255_255 } }
object { shape1 transform trans1 translate (<-2 -3*phi, 0, 1 +phi>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans2 translate (<0, 3 +5*phi, 2 +3*phi>) transform anim texture { color_240_160_0 } }
object { shape0 transform trans0 translate (<-3 -5*phi, 2 +3*phi, 0>) transform anim texture { color_255_255_255 } }
object { shape3 transform trans3 translate (<0, 1 +phi, 2 +3*phi>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans4 translate (<-2 -3*phi, 0, -3 -5*phi>) transform anim texture { color_240_160_0 } }
object { shape0 transform trans0 translate (<0, 0, 0>) transform anim texture { color_255_255_255 } }
object { shape0 transform trans0 translate (<0, -1 -phi, 2 +3*phi>) transform anim texture { color_255_255_255 } }
object { shape1 transform trans5 translate (<1 +2*phi, -1 -2*phi, 1 +2*phi>) transform anim texture { color_0_118_149 } }
object { shape0 transform trans0 translate (<3 +5*phi, -3 -5*phi, -3 -5*phi>) transform anim texture { color_255_255_255 } }
object { shape0 transform trans0 translate (<-3 -5*phi, -3 -5*phi, -3 -5*phi>) transform anim texture { color_255_255_255 } }
object { shape4 transform trans6 translate (<0, -2 -3*phi, -5 -8*phi>) transform anim texture { color_0_118_149 } }
object { shape4 transform trans7 translate (<3 +5*phi, -3 -5*phi, 3 +5*phi>) transform anim texture { color_0_118_149 } }
object { shape1 transform trans8 translate (<-1 -2*phi, -1 -2*phi, 1 +2*phi>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans5 translate (<0, -3 -5*phi, 2 +3*phi>) transform anim texture { color_240_160_0 } }
object { shape3 transform trans4 translate (<0, 1 +phi, 2 +3*phi>) transform anim texture { color_0_118_149 } }
object { shape1 transform trans9 translate (<1 +2*phi, -1 -2*phi, -1 -2*phi>) transform anim texture { color_0_118_149 } }
object { shape3 transform trans6 translate (<1 +2*phi, 1 +2*phi, -1 -2*phi>) transform anim texture { color_0_118_149 } }
object { shape3 transform trans10 translate (<0, 1 +phi, -2 -3*phi>) transform anim texture { color_0_118_149 } }
object { shape3 transform trans11 translate (<-1 -2*phi, -1 -2*phi, -1 -2*phi>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans8 translate (<-3 -5*phi, -2 -3*phi, 0>) transform anim texture { color_240_160_0 } }
object { shape0 transform trans0 translate (<1 +2*phi, 1 +2*phi, 1 +2*phi>) transform anim texture { color_255_255_255 } }
object { shape0 transform trans0 translate (<3 +5*phi, 2 +3*phi, 0>) transform anim texture { color_255_255_255 } }
object { shape1 transform trans5 translate (<0, 1 +phi, -2 -3*phi>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans12 translate (<2 +3*phi, 0, -3 -5*phi>) transform anim texture { color_240_160_0 } }
object { shape2 transform trans13 translate (<0, -3 -5*phi, -2 -3*phi>) transform anim texture { color_240_160_0 } }
object { shape2 transform trans14 translate (<2 +3*phi, 0, 3 +5*phi>) transform anim texture { color_240_160_0 } }
object { shape2 transform trans15 translate (<-2 -3*phi, 0, -3 -5*phi>) transform anim texture { color_240_160_0 } }
object { shape4 transform trans7 translate (<-2 -3*phi, -5 -8*phi, 0>) transform anim texture { color_0_118_149 } }
object { shape0 transform trans0 translate (<-2 -3*phi, 0, -1 -phi>) transform anim texture { color_255_255_255 } }
object { shape3 transform trans5 translate (<-1 -2*phi, 1 +2*phi, -1 -2*phi>) transform anim texture { color_0_118_149 } }
object { shape3 transform trans9 translate (<-1 -2*phi, 1 +2*phi, 1 +2*phi>) transform anim texture { color_0_118_149 } }
object { shape0 transform trans0 translate (<0, 3 +5*phi, 2 +3*phi>) transform anim texture { color_255_255_255 } }
object { shape0 transform trans0 translate (<-5 -8*phi, 0, 2 +3*phi>) transform anim texture { color_255_255_255 } }
object { shape3 transform trans16 translate (<1 +2*phi, -1 -2*phi, -1 -2*phi>) transform anim texture { color_0_118_149 } }
object { shape3 transform trans17 translate (<-1 -2*phi, 1 +2*phi, 1 +2*phi>) transform anim texture { color_0_118_149 } }
object { shape0 transform trans0 translate (<5 +8*phi, 0, 2 +3*phi>) transform anim texture { color_255_255_255 } }
object { shape0 transform trans0 translate (<0, 2 +3*phi, 5 +8*phi>) transform anim texture { color_255_255_255 } }
object { shape2 transform trans18 translate (<0, -3 -5*phi, 2 +3*phi>) transform anim texture { color_240_160_0 } }
object { shape3 transform trans12 translate (<-1 -2*phi, -1 -2*phi, 1 +2*phi>) transform anim texture { color_0_118_149 } }
object { shape1 transform trans13 translate (<2 +3*phi, 0, 1 +phi>) transform anim texture { color_0_118_149 } }
object { shape1 transform trans19 translate (<-1 -2*phi, -1 -2*phi, -1 -2*phi>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans20 translate (<2 +3*phi, 0, 3 +5*phi>) transform anim texture { color_240_160_0 } }
object { shape0 transform trans0 translate (<2 +3*phi, 5 +8*phi, 0>) transform anim texture { color_255_255_255 } }
object { shape0 transform trans0 translate (<2 +3*phi, 0, -3 -5*phi>) transform anim texture { color_255_255_255 } }
object { shape3 transform trans11 translate (<2 +3*phi, 0, 1 +phi>) transform anim texture { color_0_118_149 } }
object { shape1 transform trans21 translate (<-1 -phi, 2 +3*phi, 0>) transform anim texture { color_0_118_149 } }
object { shape1 transform trans22 translate (<1 +phi, -2 -3*phi, 0>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans17 translate (<0, 3 +5*phi, -2 -3*phi>) transform anim texture { color_240_160_0 } }
object { shape1 transform trans16 translate (<-1 -2*phi, 1 +2*phi, 1 +2*phi>) transform anim texture { color_0_118_149 } }
object { shape3 transform trans23 translate (<-1 -2*phi, 1 +2*phi, 1 +2*phi>) transform anim texture { color_0_118_149 } }
object { shape4 transform trans11 translate (<2 +3*phi, 5 +8*phi, 0>) transform anim texture { color_0_118_149 } }
object { shape0 transform trans0 translate (<-2 -3*phi, -5 -8*phi, 0>) transform anim texture { color_255_255_255 } }
object { shape3 transform trans24 translate (<-1 -2*phi, -1 -2*phi, -1 -2*phi>) transform anim texture { color_0_118_149 } }
object { shape3 transform trans22 translate (<-1 -phi, -2 -3*phi, 0>) transform anim texture { color_0_118_149 } }
object { shape3 transform trans25 translate (<-1 -phi, -2 -3*phi, 0>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans17 translate (<3 +5*phi, -2 -3*phi, 0>) transform anim texture { color_240_160_0 } }
object { shape1 transform trans11 translate (<1 +2*phi, 1 +2*phi, 1 +2*phi>) transform anim texture { color_0_118_149 } }
object { shape0 transform trans0 translate (<-1 -phi, 2 +3*phi, 0>) transform anim texture { color_255_255_255 } }
object { shape3 transform trans21 translate (<-1 -2*phi, 1 +2*phi, -1 -2*phi>) transform anim texture { color_0_118_149 } }
object { shape3 transform trans2 translate (<-2 -3*phi, 0, 1 +phi>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans26 translate (<3 +5*phi, 2 +3*phi, 0>) transform anim texture { color_240_160_0 } }
object { shape2 transform trans27 translate (<0, -3 -5*phi, -2 -3*phi>) transform anim texture { color_240_160_0 } }
object { shape3 transform trans28 translate (<1 +2*phi, 1 +2*phi, 1 +2*phi>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans27 translate (<-2 -3*phi, 0, 3 +5*phi>) transform anim texture { color_240_160_0 } }
object { shape3 transform trans1 translate (<-2 -3*phi, 0, -1 -phi>) transform anim texture { color_0_118_149 } }
object { shape3 transform trans3 translate (<-1 -2*phi, -1 -2*phi, -1 -2*phi>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans29 translate (<0, -3 -5*phi, -2 -3*phi>) transform anim texture { color_240_160_0 } }
object { shape0 transform trans0 translate (<-2 -3*phi, 0, 1 +phi>) transform anim texture { color_255_255_255 } }
object { shape0 transform trans0 translate (<0, 2 +3*phi, -5 -8*phi>) transform anim texture { color_255_255_255 } }
object { shape3 transform trans30 translate (<1 +2*phi, -1 -2*phi, 1 +2*phi>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans31 translate (<0, 3 +5*phi, 2 +3*phi>) transform anim texture { color_240_160_0 } }
object { shape2 transform trans14 translate (<-3 -5*phi, -2 -3*phi, 0>) transform anim texture { color_240_160_0 } }
object { shape1 transform trans3 translate (<1 +2*phi, 1 +2*phi, 1 +2*phi>) transform anim texture { color_0_118_149 } }
object { shape0 transform trans0 translate (<3 +5*phi, -2 -3*phi, 0>) transform anim texture { color_255_255_255 } }
object { shape0 transform trans0 translate (<-1 -2*phi, -1 -2*phi, 1 +2*phi>) transform anim texture { color_255_255_255 } }
object { shape3 transform trans23 translate (<0, -1 -phi, -2 -3*phi>) transform anim texture { color_0_118_149 } }
object { shape0 transform trans0 translate (<1 +2*phi, 1 +2*phi, -1 -2*phi>) transform anim texture { color_255_255_255 } }
object { shape2 transform trans8 translate (<2 +3*phi, 0, -3 -5*phi>) transform anim texture { color_240_160_0 } }
object { shape3 transform trans5 translate (<0, -1 -phi, 2 +3*phi>) transform anim texture { color_0_118_149 } }
object { shape3 transform trans32 translate (<-1 -2*phi, -1 -2*phi, 1 +2*phi>) transform anim texture { color_0_118_149 } }
object { shape3 transform trans12 translate (<2 +3*phi, 0, -1 -phi>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans20 translate (<-3 -5*phi, 2 +3*phi, 0>) transform anim texture { color_240_160_0 } }
object { shape3 transform trans15 translate (<0, -1 -phi, 2 +3*phi>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans15 translate (<3 +5*phi, -2 -3*phi, 0>) transform anim texture { color_240_160_0 } }
object { shape2 transform trans12 translate (<-3 -5*phi, -2 -3*phi, 0>) transform anim texture { color_240_160_0 } }
object { shape3 transform trans19 translate (<-2 -3*phi, 0, -1 -phi>) transform anim texture { color_0_118_149 } }
object { shape4 transform trans7 translate (<0, 2 +3*phi, 5 +8*phi>) transform anim texture { color_0_118_149 } }
object { shape3 transform trans24 translate (<1 +phi, 2 +3*phi, 0>) transform anim texture { color_0_118_149 } }
object { shape3 transform trans33 translate (<1 +phi, 2 +3*phi, 0>) transform anim texture { color_0_118_149 } }
object { shape0 transform trans0 translate (<-3 -5*phi, 3 +5*phi, 3 +5*phi>) transform anim texture { color_255_255_255 } }
object { shape2 transform trans31 translate (<-2 -3*phi, 0, -3 -5*phi>) transform anim texture { color_240_160_0 } }
object { shape0 transform trans0 translate (<1 +phi, -2 -3*phi, 0>) transform anim texture { color_255_255_255 } }
object { shape2 transform trans26 translate (<0, -3 -5*phi, -2 -3*phi>) transform anim texture { color_240_160_0 } }
object { shape4 transform trans6 translate (<-2 -3*phi, -5 -8*phi, 0>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans18 translate (<0, 3 +5*phi, 2 +3*phi>) transform anim texture { color_240_160_0 } }
object { shape3 transform trans30 translate (<-1 -phi, 2 +3*phi, 0>) transform anim texture { color_0_118_149 } }
object { shape0 transform trans0 translate (<3 +5*phi, -3 -5*phi, 3 +5*phi>) transform anim texture { color_255_255_255 } }
object { shape2 transform trans33 translate (<-3 -5*phi, 2 +3*phi, 0>) transform anim texture { color_240_160_0 } }
object { shape1 transform trans7 translate (<-1 -2*phi, 1 +2*phi, 1 +2*phi>) transform anim texture { color_0_118_149 } }
object { shape4 transform trans7 translate (<-5 -8*phi, 0, 2 +3*phi>) transform anim texture { color_0_118_149 } }
object { shape0 transform trans0 translate (<0, 3 +5*phi, -2 -3*phi>) transform anim texture { color_255_255_255 } }
object { shape2 transform trans11 translate (<0, -3 -5*phi, 2 +3*phi>) transform anim texture { color_240_160_0 } }
object { shape0 transform trans0 translate (<-5 -8*phi, 0, -2 -3*phi>) transform anim texture { color_255_255_255 } }
object { shape3 transform trans4 translate (<0, 1 +phi, -2 -3*phi>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans34 translate (<-3 -5*phi, 2 +3*phi, 0>) transform anim texture { color_240_160_0 } }
object { shape0 transform trans0 translate (<2 +3*phi, 0, 3 +5*phi>) transform anim texture { color_255_255_255 } }
object { shape3 transform trans19 translate (<1 +2*phi, 1 +2*phi, 1 +2*phi>) transform anim texture { color_0_118_149 } }
object { shape0 transform trans0 translate (<1 +phi, 2 +3*phi, 0>) transform anim texture { color_255_255_255 } }
object { shape0 transform trans0 translate (<-2 -3*phi, 0, 3 +5*phi>) transform anim texture { color_255_255_255 } }
object { shape3 transform trans8 translate (<-2 -3*phi, 0, 1 +phi>) transform anim texture { color_0_118_149 } }
object { shape3 transform trans13 translate (<-2 -3*phi, 0, -1 -phi>) transform anim texture { color_0_118_149 } }
object { shape1 transform trans32 translate (<1 +2*phi, 1 +2*phi, -1 -2*phi>) transform anim texture { color_0_118_149 } }
object { shape3 transform trans6 translate (<0, -1 -phi, 2 +3*phi>) transform anim texture { color_0_118_149 } }
object { shape0 transform trans0 translate (<0, -1 -phi, -2 -3*phi>) transform anim texture { color_255_255_255 } }
object { shape3 transform trans28 translate (<-1 -phi, -2 -3*phi, 0>) transform anim texture { color_0_118_149 } }
object { shape4 transform trans19 translate (<-2 -3*phi, -5 -8*phi, 0>) transform anim texture { color_0_118_149 } }
object { shape1 transform trans25 translate (<1 +phi, 2 +3*phi, 0>) transform anim texture { color_0_118_149 } }
object { shape0 transform trans0 translate (<-1 -2*phi, -1 -2*phi, -1 -2*phi>) transform anim texture { color_255_255_255 } }
object { shape1 transform trans22 translate (<1 +phi, 2 +3*phi, 0>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans4 translate (<-2 -3*phi, 0, 3 +5*phi>) transform anim texture { color_240_160_0 } }
object { shape2 transform trans0 translate (<2 +3*phi, 0, 3 +5*phi>) transform anim texture { color_240_160_0 } }
object { shape2 transform trans31 translate (<3 +5*phi, -2 -3*phi, 0>) transform anim texture { color_240_160_0 } }
object { shape1 transform trans23 translate (<1 +2*phi, -1 -2*phi, -1 -2*phi>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans29 translate (<-3 -5*phi, 2 +3*phi, 0>) transform anim texture { color_240_160_0 } }
object { shape2 transform trans0 translate (<0, -3 -5*phi, -2 -3*phi>) transform anim texture { color_240_160_0 } }
object { shape0 transform trans0 translate (<-3 -5*phi, -3 -5*phi, 3 +5*phi>) transform anim texture { color_255_255_255 } }
object { shape3 transform trans0 translate (<1 +phi, 2 +3*phi, 0>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans33 translate (<3 +5*phi, 2 +3*phi, 0>) transform anim texture { color_240_160_0 } }
object { shape3 transform trans25 translate (<1 +2*phi, 1 +2*phi, -1 -2*phi>) transform anim texture { color_0_118_149 } }
object { shape3 transform trans13 translate (<1 +2*phi, -1 -2*phi, 1 +2*phi>) transform anim texture { color_0_118_149 } }
object { shape3 transform trans18 translate (<1 +2*phi, -1 -2*phi, -1 -2*phi>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans21 translate (<3 +5*phi, -2 -3*phi, 0>) transform anim texture { color_240_160_0 } }
object { shape2 transform trans13 translate (<2 +3*phi, 0, 3 +5*phi>) transform anim texture { color_240_160_0 } }
object { shape0 transform trans0 translate (<-3 -5*phi, 3 +5*phi, -3 -5*phi>) transform anim texture { color_255_255_255 } }
object { shape3 transform trans15 translate (<0, -1 -phi, -2 -3*phi>) transform anim texture { color_0_118_149 } }
object { shape3 transform trans18 translate (<0, 1 +phi, 2 +3*phi>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans5 translate (<0, 3 +5*phi, 2 +3*phi>) transform anim texture { color_240_160_0 } }
object { shape3 transform trans7 translate (<1 +2*phi, -1 -2*phi, -1 -2*phi>) transform anim texture { color_0_118_149 } }
object { shape1 transform trans4 translate (<0, -1 -phi, -2 -3*phi>) transform anim texture { color_0_118_149 } }
object { shape0 transform trans0 translate (<0, 1 +phi, -2 -3*phi>) transform anim texture { color_255_255_255 } }
object { shape1 transform trans24 translate (<1 +2*phi, 1 +2*phi, 1 +2*phi>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans2 translate (<-3 -5*phi, -2 -3*phi, 0>) transform anim texture { color_240_160_0 } }
object { shape2 transform trans21 translate (<0, 3 +5*phi, -2 -3*phi>) transform anim texture { color_240_160_0 } }
object { shape2 transform trans11 translate (<3 +5*phi, 2 +3*phi, 0>) transform anim texture { color_240_160_0 } }
object { shape2 transform trans33 translate (<0, -3 -5*phi, 2 +3*phi>) transform anim texture { color_240_160_0 } }
object { shape2 transform trans17 translate (<-2 -3*phi, 0, 3 +5*phi>) transform anim texture { color_240_160_0 } }
object { shape2 transform trans29 translate (<3 +5*phi, 2 +3*phi, 0>) transform anim texture { color_240_160_0 } }
object { shape2 transform trans34 translate (<2 +3*phi, 0, -3 -5*phi>) transform anim texture { color_240_160_0 } }
object { shape2 transform trans5 translate (<-2 -3*phi, 0, -3 -5*phi>) transform anim texture { color_240_160_0 } }
object { shape3 transform trans35 translate (<1 +2*phi, 1 +2*phi, 1 +2*phi>) transform anim texture { color_0_118_149 } }
object { shape3 transform trans32 translate (<0, 1 +phi, -2 -3*phi>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans2 translate (<3 +5*phi, -2 -3*phi, 0>) transform anim texture { color_240_160_0 } }
object { shape0 transform trans0 translate (<2 +3*phi, 0, 1 +phi>) transform anim texture { color_255_255_255 } }
object { shape3 transform trans21 translate (<1 +phi, -2 -3*phi, 0>) transform anim texture { color_0_118_149 } }
object { shape0 transform trans0 translate (<0, 1 +phi, 2 +3*phi>) transform anim texture { color_255_255_255 } }
object { shape0 transform trans0 translate (<-1 -2*phi, 1 +2*phi, 1 +2*phi>) transform anim texture { color_255_255_255 } }
object { shape3 transform trans35 translate (<0, -1 -phi, -2 -3*phi>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans11 translate (<-2 -3*phi, 0, -3 -5*phi>) transform anim texture { color_240_160_0 } }
object { shape0 transform trans0 translate (<3 +5*phi, 3 +5*phi, -3 -5*phi>) transform anim texture { color_255_255_255 } }
object { shape4 transform trans11 translate (<-3 -5*phi, 3 +5*phi, -3 -5*phi>) transform anim texture { color_0_118_149 } }
object { shape0 transform trans0 translate (<2 +3*phi, 0, -1 -phi>) transform anim texture { color_255_255_255 } }
object { shape3 transform trans20 translate (<-1 -2*phi, 1 +2*phi, -1 -2*phi>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans21 translate (<-3 -5*phi, -2 -3*phi, 0>) transform anim texture { color_240_160_0 } }
object { shape0 transform trans0 translate (<0, -3 -5*phi, -2 -3*phi>) transform anim texture { color_255_255_255 } }
object { shape1 transform trans15 translate (<0, 1 +phi, 2 +3*phi>) transform anim texture { color_0_118_149 } }
object { shape3 transform trans1 translate (<2 +3*phi, 0, -1 -phi>) transform anim texture { color_0_118_149 } }
object { shape3 transform trans17 translate (<2 +3*phi, 0, -1 -phi>) transform anim texture { color_0_118_149 } }
object { shape4 transform trans19 translate (<3 +5*phi, -3 -5*phi, 3 +5*phi>) transform anim texture { color_0_118_149 } }
object { shape1 transform trans32 translate (<0, -1 -phi, 2 +3*phi>) transform anim texture { color_0_118_149 } }
object { shape0 transform trans0 translate (<0, -2 -3*phi, -5 -8*phi>) transform anim texture { color_255_255_255 } }
object { shape2 transform trans4 translate (<3 +5*phi, 2 +3*phi, 0>) transform anim texture { color_240_160_0 } }
object { shape2 transform trans12 translate (<2 +3*phi, 0, 3 +5*phi>) transform anim texture { color_240_160_0 } }
object { shape1 transform trans3 translate (<0, -1 -phi, -2 -3*phi>) transform anim texture { color_0_118_149 } }
object { shape3 transform trans16 translate (<-2 -3*phi, 0, 1 +phi>) transform anim texture { color_0_118_149 } }
object { shape3 transform trans33 translate (<-1 -2*phi, -1 -2*phi, 1 +2*phi>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans26 translate (<-2 -3*phi, 0, 3 +5*phi>) transform anim texture { color_240_160_0 } }
object { shape3 transform trans10 translate (<1 +2*phi, -1 -2*phi, 1 +2*phi>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans34 translate (<0, -3 -5*phi, 2 +3*phi>) transform anim texture { color_240_160_0 } }
object { shape2 transform trans18 translate (<2 +3*phi, 0, -3 -5*phi>) transform anim texture { color_240_160_0 } }
object { shape0 transform trans0 translate (<1 +2*phi, -1 -2*phi, -1 -2*phi>) transform anim texture { color_255_255_255 } }
object { shape0 transform trans0 translate (<0, -3 -5*phi, 2 +3*phi>) transform anim texture { color_255_255_255 } }
object { shape2 transform trans15 translate (<-2 -3*phi, 0, 3 +5*phi>) transform anim texture { color_240_160_0 } }
object { shape1 transform trans16 translate (<2 +3*phi, 0, -1 -phi>) transform anim texture { color_0_118_149 } }
object { shape3 transform trans0 translate (<1 +phi, -2 -3*phi, 0>) transform anim texture { color_0_118_149 } }
object { shape3 transform trans9 translate (<1 +phi, -2 -3*phi, 0>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans27 translate (<0, 3 +5*phi, -2 -3*phi>) transform anim texture { color_240_160_0 } }
object { shape3 transform trans20 translate (<2 +3*phi, 0, 1 +phi>) transform anim texture { color_0_118_149 } }
object { shape0 transform trans0 translate (<0, -2 -3*phi, 5 +8*phi>) transform anim texture { color_255_255_255 } }
object { shape2 transform trans13 translate (<-3 -5*phi, 2 +3*phi, 0>) transform anim texture { color_240_160_0 } }
object { shape1 transform trans8 translate (<2 +3*phi, 0, -1 -phi>) transform anim texture { color_0_118_149 } }
object { shape1 transform trans1 translate (<2 +3*phi, 0, 1 +phi>) transform anim texture { color_0_118_149 } }
object { shape3 transform trans8 translate (<1 +2*phi, 1 +2*phi, -1 -2*phi>) transform anim texture { color_0_118_149 } }
object { shape3 transform trans2 translate (<2 +3*phi, 0, 1 +phi>) transform anim texture { color_0_118_149 } }
object { shape0 transform trans0 translate (<5 +8*phi, 0, -2 -3*phi>) transform anim texture { color_255_255_255 } }
object { shape0 transform trans0 translate (<2 +3*phi, -5 -8*phi, 0>) transform anim texture { color_255_255_255 } }
object { shape1 transform trans23 translate (<0, 1 +phi, 2 +3*phi>) transform anim texture { color_0_118_149 } }
object { shape1 transform trans21 translate (<1 +2*phi, -1 -2*phi, 1 +2*phi>) transform anim texture { color_0_118_149 } }
object { shape3 transform trans7 translate (<-1 -phi, 2 +3*phi, 0>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans20 translate (<2 +3*phi, 0, -3 -5*phi>) transform anim texture { color_240_160_0 } }
object { shape4 transform trans32 translate (<0, 2 +3*phi, 5 +8*phi>) transform anim texture { color_0_118_149 } }
object { shape0 transform trans0 translate (<1 +2*phi, -1 -2*phi, 1 +2*phi>) transform anim texture { color_255_255_255 } }
object { shape0 transform trans0 translate (<-3 -5*phi, -2 -3*phi, 0>) transform anim texture { color_255_255_255 } }
object { shape4 transform trans32 translate (<2 +3*phi, 5 +8*phi, 0>) transform anim texture { color_0_118_149 } }
object { shape3 transform trans22 translate (<-1 -phi, 2 +3*phi, 0>) transform anim texture { color_0_118_149 } }
object { shape0 transform trans0 translate (<-1 -2*phi, 1 +2*phi, -1 -2*phi>) transform anim texture { color_255_255_255 } }
object { shape0 transform trans0 translate (<3 +5*phi, 3 +5*phi, 3 +5*phi>) transform anim texture { color_255_255_255 } }
object { shape0 transform trans0 translate (<-1 -phi, -2 -3*phi, 0>) transform anim texture { color_255_255_255 } }
object { shape1 transform trans20 translate (<-2 -3*phi, 0, -1 -phi>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans8 translate (<0, 3 +5*phi, 2 +3*phi>) transform anim texture { color_240_160_0 } }
object { shape1 transform trans33 translate (<-1 -phi, -2 -3*phi, 0>) transform anim texture { color_0_118_149 } }
object { shape1 transform trans24 translate (<-1 -phi, -2 -3*phi, 0>) transform anim texture { color_0_118_149 } }
object { shape2 transform trans14 translate (<0, 3 +5*phi, -2 -3*phi>) transform anim texture { color_240_160_0 } }
object { shape2 transform trans0 translate (<0, 3 +5*phi, -2 -3*phi>) transform anim texture { color_240_160_0 } }

