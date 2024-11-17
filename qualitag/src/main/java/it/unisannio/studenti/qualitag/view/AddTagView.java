package it.unisannio.studenti.qualitag.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import it.unisannio.studenti.qualitag.model.Tag;
import it.unisannio.studenti.qualitag.repository.TagRepository;
import java.util.HashSet;
import java.util.Set;

@Route("")
public class AddTagView extends VerticalLayout {

  private final TagRepository repo;
  private Set<Tag> selectedTags;

  public AddTagView(TagRepository repo) {
    this.repo = repo;
    this.selectedTags = new HashSet<>();

    var title = new H1("Add a new tag");
    var value = new TextField();
    var tag_list = new VerticalLayout();
    var confirmButton = new Button("Save tag");
    confirmButton.addClickListener(click -> {
      // Add the tag to the database
      var tag = repo.save(new Tag(value.getValue(), "test_project_id", "test_user_id"));
      tag_list.add(createTagView(tag));
    });

    var deleteButton = new Button("Delete selected tags");
    deleteButton.addClickListener(click -> {
      // Delete the selected tags from the database
      repo.deleteAll(selectedTags);
      selectedTags.clear();
      tag_list.removeAll();
      repo.findAll().forEach(tag -> tag_list.add(createTagView(tag)));
    });

    add(
      title,
      new HorizontalLayout(
          new HorizontalLayout(value, confirmButton),
          new VerticalLayout(new H2("Here we will show the tags"))

      )
    );
  }

  private Component createTagView(Tag tag) {
    Checkbox checkbox = new Checkbox(tag.getTag_value());
    checkbox.addValueChangeListener(event -> {
      if (event.getValue()) {
        selectedTags.add(tag);
      } else {
        selectedTags.remove(tag);
      }
    });
    return checkbox;
  }

}
