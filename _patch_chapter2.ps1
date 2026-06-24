$ErrorActionPreference = 'Stop'
$base = Split-Path -Parent $MyInvocation.MyCommand.Path
$docPath = Join-Path $base ([char]0x0434 + [char]0x0438 + [char]0x043F + [char]0x043B + [char]0x043E + [char]0x043C + '.docx')
if (-not (Test-Path -LiteralPath $docPath)) {
    $docPath = Get-ChildItem -LiteralPath $base -Filter '*.docx' |
        Where-Object { $_.Name -notmatch 'backup|слиян' } |
        Sort-Object Length -Descending |
        Select-Object -First 1 -ExpandProperty FullName
}
$contentPath = Join-Path $base '_chapter_2_full.txt'
$markersPath = Join-Path $base '_chapter2_markers.txt'

if (-not (Test-Path -LiteralPath $docPath)) { throw "docx not found" }
if (-not (Test-Path -LiteralPath $contentPath)) { throw "content not found" }

$content = [System.IO.File]::ReadAllText($contentPath, [System.Text.Encoding]::UTF8)
$markers = [System.IO.File]::ReadAllLines($markersPath, [System.Text.Encoding]::UTF8)
$ch2Marker = $markers[0].Trim()
$ch3Marker = $markers[1].Trim()

$word = New-Object -ComObject Word.Application
$word.Visible = $false
$doc = $word.Documents.Open($docPath)

try {
    $full = $doc.Content.Text
    if (-not $full.Contains($ch2Marker)) { throw "Ch2 marker not found: $ch2Marker" }
    if (-not $full.Contains($ch3Marker)) { throw "Ch3 marker not found: $ch3Marker" }

    $r2 = $doc.Content.Duplicate
    $r2.Find.ClearFormatting() | Out-Null
    if (-not $r2.Find.Execute($ch2Marker)) { throw 'Find Ch2 failed' }
    $ch2Start = $r2.Start

    $r3 = $doc.Content.Duplicate
    $r3.Find.ClearFormatting() | Out-Null
    if (-not $r3.Find.Execute($ch3Marker)) { throw 'Find Ch3 failed' }
    $ch3Start = $r3.Start

    if ($ch3Start -le $ch2Start) { throw 'Invalid chapter bounds' }

    $replaceRange = $doc.Range($ch2Start, $ch3Start)
    $newText = $ch2Marker + "`r`n`r`n" + $content + "`r`n"
    $replaceRange.Text = $newText

    $doc.Save()
    Write-Output "Chapter 2 updated: $docPath"
}
finally {
    $doc.Close()
    $word.Quit()
    [System.Runtime.Interopservices.Marshal]::ReleaseComObject($word) | Out-Null
}
