param(
    [string]$Source = (Join-Path $PSScriptRoot '../artwork/freedocs-logo-transparent.png')
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing

if (-not ('FreeDocsIconAssets' -as [type])) {
    Add-Type -ReferencedAssemblies System.Drawing -TypeDefinition @'
using System;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Drawing.Imaging;
using System.IO;

public static class FreeDocsIconAssets {
    // The source already has alpha from imagegen. This only packs/resizes Android assets.
    public static Rectangle Bounds(Bitmap image) {
        int left=image.Width, top=image.Height, right=-1, bottom=-1;
        for (int y=0; y<image.Height; y++) for (int x=0; x<image.Width; x++) {
            if (image.GetPixel(x,y).A == 0) continue;
            left=Math.Min(left,x); top=Math.Min(top,y);
            right=Math.Max(right,x); bottom=Math.Max(bottom,y);
        }
        if (right<left) throw new InvalidDataException("The logo is fully transparent.");
        return Rectangle.FromLTRB(left,top,right+1,bottom+1);
    }

    public static void Save(Bitmap source, Rectangle bounds, string file, int size, int content) {
        Directory.CreateDirectory(Path.GetDirectoryName(file));
        using (var output=new Bitmap(size,size,PixelFormat.Format32bppArgb)) {
            using (var graphics=Graphics.FromImage(output)) {
                graphics.Clear(Color.Transparent);
                graphics.CompositingQuality=CompositingQuality.HighQuality;
                graphics.InterpolationMode=InterpolationMode.HighQualityBicubic;
                graphics.PixelOffsetMode=PixelOffsetMode.HighQuality;
                double factor=Math.Min((double)content/bounds.Width,(double)content/bounds.Height);
                int width=(int)Math.Round(bounds.Width*factor), height=(int)Math.Round(bounds.Height*factor);
                graphics.DrawImage(source,new Rectangle((size-width)/2,(size-height)/2,width,height),bounds,GraphicsUnit.Pixel);
            }
            output.Save(file,ImageFormat.Png);
        }
    }
}
'@
}

$readerProject = Split-Path -Parent $PSScriptRoot
$readerResources = Join-Path $readerProject 'app/src/main/res'
$readerLogo = [System.Drawing.Bitmap]::FromFile((Resolve-Path -LiteralPath $Source).Path)
try {
    $readerBounds = [FreeDocsIconAssets]::Bounds($readerLogo)
    $readerDensities = @{ mdpi=1.0; hdpi=1.5; xhdpi=2.0; xxhdpi=3.0; xxxhdpi=4.0 }
    foreach ($readerDensity in $readerDensities.GetEnumerator()) {
        $readerScale = $readerDensity.Value
        [FreeDocsIconAssets]::Save($readerLogo, $readerBounds,
            (Join-Path $readerResources "mipmap-$($readerDensity.Key)/ic_launcher.png"),
            [int](48*$readerScale), [int](44*$readerScale))
        # Padding keeps the full logo inside the Android 12 splash mask.
        [FreeDocsIconAssets]::Save($readerLogo, $readerBounds,
            (Join-Path $readerResources "drawable-$($readerDensity.Key)/freedocs_splash.png"),
            [int](288*$readerScale), [int](144*$readerScale))
    }
    [FreeDocsIconAssets]::Save($readerLogo, $readerBounds,
        (Join-Path $readerResources 'drawable-xxxhdpi/ic_launcher_foreground.png'), 432, 200)
    [FreeDocsIconAssets]::Save($readerLogo, $readerBounds,
        (Join-Path $readerResources 'drawable-xxxhdpi/freedocs_logo.png'), 576, 576)
    Write-Output 'Prepared launcher and splash PNG assets with transparent alpha.'
} finally { $readerLogo.Dispose() }
