package org.exoplatform.ide.tree;

import com.google.gwt.core.client.GWT;

import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.resources.model.File;
import org.exoplatform.ide.resources.model.Folder;
import org.exoplatform.ide.resources.model.Resource;

public class ResourceTreeNodeDataAdapter implements NodeDataAdapter<Resource>
{
   @Override
   public int compare(Resource a, Resource b)
   {
      return a.getPath().compareTo(b.getPath());
   }

   @Override
   public boolean hasChildren(Resource data)
   {
      return data.isFolder() && ((Folder)data).getChildren().size()>0;
   }

   @Override
   public JsonArray<Resource> getChildren(Resource data)
   {
      if (data instanceof Folder)
      {
         return ((Folder)data).getChildren();
      }
      return null;
   }

   @Override
   public String getNodeId(Resource data)
   {
      return data.getId();
   }

   @Override
   public String getNodeName(Resource data)
   {
      return data.getName();
   }

   @Override
   public Resource getParent(Resource data)
   {
      return data.getParent();
   }

   @Override
   public TreeNodeElement<Resource> getRenderedTreeNode(Resource data)
   {
      return (TreeNodeElement<Resource>)data.getTag();
   }

   @Override
   public void setNodeName(Resource data, String name)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void setRenderedTreeNode(Resource data, TreeNodeElement<Resource> renderedNode)
   {
      data.setTag(renderedNode);
   }

   @Override
   public Resource getDragDropTarget(Resource data)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public JsonArray<String> getNodePath(Resource data)
   {
      JsonArray<String> list = JsonCollections.<String> createArray();
      JsonArray<String> result = JsonCollections.<String> createArray();
      list.add(data.getId());

      Resource localData = data;
      while (localData.getParent() != null)
      {
         localData.getParent().getId();
         localData = localData.getParent();
      }

      for (int i = list.size(); i > 0; i--)
      {
         result.add(list.get(i-1));
      }
      return result;
   }

   @Override
   public Resource getNodeByPath(Resource root, JsonArray<String> relativeNodePath)
   {
      if (root instanceof Folder)
      {
         Folder localRoot = (Folder)root;
         for (int i = 0; i < relativeNodePath.size(); i++)
         {
            if (localRoot != null)
            {
               Resource findResourceById = localRoot.findResourceById(relativeNodePath.get(i));
               if (findResourceById instanceof Folder)
               {
                  localRoot = (Folder)findResourceById;
               }
               if (findResourceById instanceof File)
               {
                  if (i == (relativeNodePath.size() - 1))
                  {
                     return findResourceById;
                  }
               }
            }
         }
      }
      return null;
   }
}
