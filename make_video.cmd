cd outputs
ffmpeg -framerate 10 -i "f%d.bmp" -c:v libx264 -pix_fmt yuv420p output.mp4