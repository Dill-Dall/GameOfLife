//---------------------------------------------INSTRUCTIONS--------------------------------------------------//

	-Draw or run patterns on the canvas. Place cells with the left mouse button.

	-Start: starts the generation cyclus. 

	-Reset: starts opens a new game.
	
	-SIZE OF CELLS: Use size slider to adjust size of canvas or 
	        use the mouse wheel. 

	-FIT ZOOM: Adjust the zoom to fit the current width
       		   and heigh of the board.

	-Drag Board: Hold right mouse button on canvas and drag it to drag the view of the canvas around.

	-THREAD: Change between running multiple threads to go to the next generation of 
       		 the pattern or use one thread which is optimised. Unthreaded tends to run
		 better on boards where there are relative few alive cells compared to
		 dead cells.
	
	-CHOOSE FILE: Open a .rle file with a new pattern. Also can take most .lif and similiar formats.
	-CHOOSE URL: Opens a pattern rom a url where the textis formatted.
		     to an .rle file.
	
			#Place pattern on the board with 
	
					|W|
				    |A| |S| |D|		
	
		#Rotate pattern with |Q|-left  |r| - right


	-RULESET: Choose ruleset. B(n) means a cell will be born if it has n alive cells as neighbors.
				  S(n) means an alive cell will survive if it has n alive cells as 
			          neighbors.n can be multiple number values.

	-SET Custom rule: Type a rule and press ENTER. rule format B(n)/S(n). n can be mulitple digits.
				  example: B13/S05 - means that a dead cell will be born with 1 or 3 
			          neighbors and an alive will die with 5 or 0 neighbors.

	-ERASE: Sets the mouse to erase alive cells (kill) when pushing left mouse button on the board. 
		Check box or hold ctrl while clicking.

	-STATISTICS: Use on an unthreaded method to show a set nember of iterations and how to pattern 
	             will evolve.

	- MUSIC: Choose background music .waw file.
             
	-OPEN EDITOR: Adjust the pattern, and in real time see how the next 20 iterations of it will be.
              Can save the pattern as a .rle file with user comments.
	      Also can save the pattern as a GIF file.
	
//------------------------------------------------------------------------------------------------------------//