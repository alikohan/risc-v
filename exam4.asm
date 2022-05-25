add 	x1, x2, x3
sub 	x2, x1, x3
add 	x3, x4, x3
ld  	x1, 0(x0)
sub 	x2, x1, x3
add		x2, x1, x4

add x4, x1, x1
ld    x2, 8(x0)
add 		x3, x1, x2
sub x4, x3, x2

sd  	x3, 24(x0)

ld  	x4, 16(x0)
add 	x5, x4, x3

sd  	x5, 32(x0)