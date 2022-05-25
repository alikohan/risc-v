ld  	x1, 0(x0)

ld  	x2, 8(x0)
add 	x3,x1,x2

sd  	x3, 24(x0)
ld  	x4, 16(x0)

add 	x5, x1, x4
sd  	x5, 32(x0)