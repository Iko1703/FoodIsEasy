$ErrorActionPreference = 'Stop'
$base = Split-Path -Parent $MyInvocation.MyCommand.Path
$path = Get-ChildItem -LiteralPath $base -Filter '*.docx' |
    Where-Object { $_.Name -notmatch 'backup|слиян' } |
    Sort-Object Length -Descending |
    Select-Object -First 1 -ExpandProperty FullName
if (-not $path) { throw "docx not found in $base" }
Write-Output "Patching: $path"
$strings = @{}
Get-Content (Join-Path $base '_patch_ru.txt') -Encoding UTF8 | ForEach-Object {
    if ($_ -match '^([^=]+)=(.*)$') { $strings[$Matches[1]] = $Matches[2] }
}

$word = New-Object -ComObject Word.Application
$word.Visible = $false
$doc = $word.Documents.Open($path)

function Remove-ParagraphIfContains([string]$needle) {
    for ($i = $doc.Paragraphs.Count; $i -ge 1; $i--) {
        $t = ($doc.Paragraphs.Item($i).Range.Text -replace "`r`n$", '').Trim()
        if ($t.Contains($needle)) {
            $doc.Paragraphs.Item($i).Range.Delete() | Out-Null
            return $true
        }
    }
    return $false
}

function Find-SecondRange([string]$needle) {
    $r = $doc.Content
    $seen = $false
    while ($true) {
        $fr = $r.Duplicate
        $fr.Find.ClearFormatting() | Out-Null
        if (-not $fr.Find.Execute($needle)) { return $null }
        if (-not $seen) { $seen = $true; $r.SetRange($fr.End, $doc.Content.End); continue }
        return $fr
    }
}

function Insert-AfterFind([string]$needle, [string]$text, [string]$guard) {
    if ($doc.Content.Text.Contains($guard)) { return }
    $r = $doc.Content.Duplicate
    if (-not $r.Find.Execute($needle)) { return }
    $r.Collapse(0)
    $r.InsertAfter("`r`n$text`r`n`r`n") | Out-Null
}

function Replace-First([string]$old, [string]$new) {
    if (-not $doc.Content.Text.Contains($old)) { return }
    $rng = $doc.Content.Duplicate
    $f = $rng.Find
    $f.ClearFormatting() | Out-Null
    $f.Text = $old
    $f.Forward = $true
    $f.Wrap = 1
    if ($f.Execute()) {
        $rng.Text = $new
    }
}

Remove-ParagraphIfContains $strings['REMOVE_META'] | Out-Null

$dup = Find-SecondRange $strings['DUP_START']
if ($dup) {
    $ch2 = $doc.Content.Duplicate
    if ($ch2.Find.Execute($strings['CH2_HEAD'])) {
        $doc.Range($dup.Start, $ch2.Start).Delete() | Out-Null
        Write-Output 'Removed duplicate 1.2'
    }
}

Replace-First $strings['FIX_OLD'] $strings['FIX_NEW']

Insert-AfterFind $strings['NEEDLE13'] $strings['FIG13'] $strings['GUARD13']
Insert-AfterFind $strings['NEEDLE14'] $strings['FIG14'] $strings['GUARD14']
Insert-AfterFind $strings['NEEDLE15'] $strings['FIG15'] $strings['GUARD15']
Insert-AfterFind $strings['NEEDLE16'] $strings['FIG16'] $strings['GUARD16']

if (-not $doc.Content.Text.Contains($strings['GUARD_CONC'])) {
    $r = $doc.Content.Duplicate
    if ($r.Find.Execute($strings['SEC14'])) {
        $r.InsertBefore("$($strings['CONCLUSION'])`r`n`r`n") | Out-Null
    }
}

Replace-First $strings['CH2_OLD'] $strings['CH2_NEW']
Replace-First $strings['OLD21'] $strings['NEW21']

if (-not $doc.Content.Text.Contains($strings['GUARD23'])) {
    Replace-First $strings['OLD23'] $strings['NEW23']
}

if (-not $doc.Content.Text.Contains($strings['GUARD_ER'])) {
    Replace-First $strings['OLD_ER'] $strings['NEW_ER']
}

if (-not $doc.Content.Text.Contains($strings['GUARD248'])) {
    Replace-First $strings['OLD248'] $strings['NEW248']
}

if (-not $doc.Content.Text.Contains($strings['GUARD31'])) {
    Replace-First $strings['OLD31'] $strings['NEW31']
}

if (-not $doc.Content.Text.Contains($strings['GUARD_BRIDGE'])) {
    $r = $doc.Content.Duplicate
    if ($r.Find.Execute($strings['SEC13'])) {
        $r.Collapse(1)
        $r.InsertBefore("$($strings['BRIDGE'])`r`n`r`n") | Out-Null
    }
}

$doc.Save()
$doc.Close()
$word.Quit()
Write-Output 'SAVED'
