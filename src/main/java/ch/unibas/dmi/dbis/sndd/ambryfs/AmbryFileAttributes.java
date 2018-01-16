package ch.unibas.dmi.dbis.sndd.ambryfs;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

/**
 * @author silvan on 16.01.18.
 */
public class AmbryFileAttributes implements BasicFileAttributes {


  private final long size;

  AmbryFileAttributes(long size){
    this.size = size;
  }

  @Override
  public FileTime lastModifiedTime() {
    return null;
  }

  @Override
  public FileTime lastAccessTime() {
    return null;
  }

  @Override
  public FileTime creationTime() {
    return null;
  }

  @Override
  public boolean isRegularFile() {
    return true;
  }

  @Override
  public boolean isDirectory() {
    return false; //Ambry has no directories
  }

  @Override
  public boolean isSymbolicLink() {
    return false;
  }

  @Override
  public boolean isOther() {
    return false;
  }

  @Override
  public long size() {
    return size;
  }

  @Override
  public Object fileKey() {
    return null;
  }
}
