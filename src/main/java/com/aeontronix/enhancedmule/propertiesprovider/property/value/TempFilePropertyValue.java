package com.aeontronix.enhancedmule.propertiesprovider.property.value;

import com.aeontronix.commons.StringUtils;
import com.aeontronix.commons.exception.UnexpectedException;
import com.aeontronix.commons.file.FileUtils;
import com.aeontronix.commons.file.TempFile;
import com.aeontronix.commons.io.IOUtils;
import com.aeontronix.enhancedmule.propertiesprovider.property.PropertyResolutionException;

import java.io.IOException;

public class TempFilePropertyValue extends PropertyValue {
    protected TempFile tempFile;
    private PropertyValue contentValue;
    private boolean secure;

    public TempFilePropertyValue(PropertyValue contentValue) {
        this.contentValue = contentValue;
        this.secure = true;
    }

    @Override
    public void close() throws IOException {
        deleteTempFile();
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public String getRawValue() {
        return "[GENERATED TEMP FILE]";
    }

    private void deleteTempFile() {
        IOUtils.close(tempFile);
        tempFile = null;
    }

    @Override
    public String evaluate() throws PropertyResolutionException {
        if (tempFile == null) {
            try {
                tempFile = new TempFile("propfile");
                FileUtils.write(tempFile, StringUtils.base64Decode(contentValue.evaluate()));
                return tempFile.getAbsolutePath();
            } catch (IOException e) {
                throw new UnexpectedException(e);
            }
        }
        return tempFile.getAbsolutePath();
    }
}
