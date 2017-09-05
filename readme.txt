
UTFØRTE ARBEIDS OPPGAVER:

1: Planlegging og oppsett.
2: Datastruktur og grafikk.
3: Animasjon.
4: Testing og dokumentering.
		Utvidelse: Optimalisert ved at en levende celle legger til +1 på nabocellenes verdi på nabobrettet.
				Istedet for at hver celle teller sine egne naboer selv om de er døde.
				Også optimalisert (kun ved en tråd) ved at rad og kollonne indeksene på til hver aktiv
				celle  (dvs levende eller med naboer) lagres i en index objekter i en ArrayList. Dette
				Medfører at det bare itereres gjennom disse for hver generation. Noe som gir en mye mer
				optimalisert ytelse. Går spesielt raskere når det er mye færre levende celler enn døde.
				Går raskere enn flere tråder opp til ett punkt.
				
				Valg av regelsett: Vi har implementert valg av kjente regelsett og muligheter for å sette,
				egne regler. Laget en robust rle leser som skal kunne tolke de fleste standardformat.	
				
#:	Evaluering og Obligatorisk oppg 1 godkjent.

5:	Filbehandling og unntak.
		Utvidelse: Kan plassere nye rle brett oppå eksisterende brett. Plassere dem med: W,A,S,D, rotere med Q og E.
				Og installere oppå ekstisterende med Enter.
				Implementert Move and Drag av brettet med høyre mustast.
		
		Obligatorisk oppg 2 godkjent.
		
6:	Dynamisk brett.
7:	Samtidig programmering.
		Kommentar: Løst ved å lage dobbelt så mange tråder som tilgjengelige prossesorer. Deretter kjører halvparten av trådene på
			annenhvert kollonne område på brettet, deretter kjører de resterende trådene på de ikke kjørte kollonnene. Dette gjør at 
			synkronisering problematikk ikke oppstår, og at vi slipper å bruke tidkrevende synkronisering på metoder.
		
UTVIDELSE: Animasjon og gif.
		Kommentar: Alt utført, bruker fremdeles StaticBoard i denne. I tilegg endret slik at reglene for brettet 
			kan endres i samtid på de 20 neste iterasjonsbildene i editor.

UTVIDELSE: Android.
		ALt utført. Se vedlagte kodefiler og pdf.
	
UTVIDELSE: STATISTIKK OG LYD
		Kommentar: Presentasjon av statistikk med antall levende celler, endring i antall levende celler og likhetsmåling er utført.
			Bruk av statistikk: har laget metodene for å finne duplikat iterasjon.
			Generering av lyd via sinus funksjon er utført, men kombinering av bølgene lykkes ikke.
			Funksjon for å la bruker velge, start og stopp musikk er utført.

Ved produksjonen av .JAR-filen opplevde vi problemer med å lese av enkelte filer som ikke har vært et problem i Netbeans 8.2.
Vi fikk løst en del og med mer tid ville vi forsatt med å forsøke å bruke BufferedReader for løse dette.
Samtidig måtte vi slette musikkfiler for å få en mindre fil til levering.



	