$(document).on('click.tree.data-api', '.tree[data-toggle="tree"] span i', function (e) {
  e.preventDefault();
  var children = $(this).parent('span').parent('li.parent_li').find(' > ul > li');
  if (children.is(':visible')) {
    children.hide('fast');
    $(this).addClass('icon-plus-sign').removeClass('icon-minus-sign');
  }
  else {
    children.show('fast');
    $(this).addClass('icon-minus-sign').removeClass('icon-plus-sign');
  }
  e.stopPropagation();
});


$(document).on('click.tree.data-api', '.tree[data-toggle="tree"] span', function (e) {
	  e.preventDefault();
	  $(this).closest('div.tree').find('li span').removeClass('active');
	  $(this).addClass('active');
	  e.stopPropagation();
});
