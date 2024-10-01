git branch --merged | ForEach-Object {
    $branchName = $_.Trim()

    if ($branchName.ToLower() -ne 'dev' -and 
        $branchName.ToLower() -ne '* dev' -and 
        $branchName.ToLower() -ne 'master' -and 
        $branchName.ToLower() -ne 'main') {

        git branch -d $branchName
    }
}
