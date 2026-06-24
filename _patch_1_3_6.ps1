$ErrorActionPreference = 'Stop'
$docPath = 'C:\Users\Iko\Desktop\глава 1 апдейт.docx'
$textPath = Join-Path $PSScriptRoot '_section_1_3_6.txt'
if (-not (Test-Path -LiteralPath $docPath)) { throw "Not found: $docPath" }

$newParas = Get-Content $textPath -Encoding UTF8

$word = New-Object -ComObject Word.Application
$word.Visible = $false
$doc = $word.Documents.Open($docPath)

$start = $null
$end = $null
for ($i = 1; $i -le $doc.Paragraphs.Count; $i++) {
    $t = ($doc.Paragraphs.Item($i).Range.Text -replace "`r`n$", '').Trim()
    if ($t -match '^1\.3\.6\.' -and -not $start) { $start = $i }
    if ($t -match '^1\.4\s' -and $start) { $end = $i; break }
}
if (-not $start -or -not $end) {
    $doc.Close($false); $word.Quit()
    throw "Could not find 1.3.6 .. 1.4 (start=$start end=$end)"
}

$rng = $doc.Range(
    $doc.Paragraphs.Item($start).Range.Start,
    $doc.Paragraphs.Item($end).Range.Start
)
$rng.Delete() | Out-Null

$insertAt = $doc.Paragraphs.Item($start).Range
$insertAt.Collapse(1)
foreach ($line in $newParas) {
    $insertAt.InsertAfter("$line`r`n") | Out-Null
    $insertAt.Collapse(0)
}

$doc.Save()
$doc.Close()
$word.Quit()
Write-Output "Replaced paragraphs $start..$($end-1) with new 1.3.6"
