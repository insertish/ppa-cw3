pdflatex report.tex
pwsh generate_code.ps1
pdftk report.pdf code.pdf data.pdf cat output "Final Report.pdf"