#Image Store


This project uses image hosting providers like flickr and facebook as cloud data storage. The main issue in exploiting image hosts as cloud storage, is that most providers use jpeg-compression on uploaded images. Because of that, it is important to chose a encoding that is resistant to this compression and can be decoded even the image was compressed.
This implementation comes with different byte encodings for the image-generation. In the normal case, BufferedImages of type TYPE_INT_RGB are used to store the input bytes. Some implementations can use more primitive image types which has some influnce on the output image size.

##Flickr (Fl), Facebook (FB)

A list of the different byte-painters:

###Normal(1Layer):
	
* BinaryBytesToImagePainter
This byte-painter uses two colors to map the binary representation of the input bytes into an image. Because only two Colors are needed (e.g. black&white), a BufferedImage of the type TYPE_BYTE_BINARY can be used: 
>1Byte needs 8Pixels. (Fl), (FB)

* QuaternaryBytesToImagePainter
This byte-painter uses four colors to map the quaternary representation of the input bytes into an image. Because not more than four colors are needed, a BufferedImage of the type TYPE_BYTE_GREY can be used.
>1Byte needs 4Pixels.	(Fl), (FB)

* SeptenaryLayeredBytesToImagePainter
This byte-painter uses seven colors to map the quaternary representation of the input bytes into an image.
>1Byte needs 3Pixels. (Fl), (FB-)

* HexadecimalBytesToImagePainter
This byte-painter uses sixteen colors to map the hexadecimal representation of the input bytes into an image.
>1Byte needs 2Pixels. (Fl), (FB-)
		

###Layered:
	
* QuaternaryLayeredBytesToImagePainter
This byte-painter uses three layers with four different colors for each layer to map the quaternary representation of the input bytes into an image. 
>1Byte needs 4/3Pixels. (Fl), (FB-)

* SeptenaryLayeredBytesToImagePainter 
This byte-painter uses three layers with seven different colors for each layer to map the septenary representation of the input bytes into an image.
>1Byte needs 1Pixel. (Fl+), (FB-)

* HexadecimalLayeredBytesToImagePainter
This byte-painter uses three layers with sixteen different colors for each layer to map the hexadecimal representation of the input bytes into an image.
>1Byte needs 1/2Pixel (Fl-), (FB-)
		
##Layered & Color Alternation
		
* OctalLayeredColorAlternatingBytesToImagePainter
This byte-painter uses three layers with eight different colors for each layer to map the octal representation of the input bytes into an image. Also alternating colors are used to mark the length of each byte in the image. This makes it possible to use the pixels in the image more efficient. The gain of this technique strongly depends on the input bytes.
>1Byte needs < 1Pixel. (Fl-), (FB-)
		
##FAQ

* How does the bytes to image mapping work?
Here is a simple example: Say we have a byte b = 86 and we want this byte to be mapped into an image. The binary representation of b is: 01010110. We could now map this binary representation into the pixels of a black & white image, where black stands for the binary 0 white for the binary 1. Pixels in the image representing the binary representation of 86 would be: 

```
	Pixels:	[b][w][b][w][b][w][w][b]
	Binary:  0  1  0  1  0  1  1  0
	w = white <=> binary 1
	b = black <=> binary 0
```
	
* Where does the naming of the byte-painters come from?
http://en.wikipedia.org/wiki/List_of_numeral_systems